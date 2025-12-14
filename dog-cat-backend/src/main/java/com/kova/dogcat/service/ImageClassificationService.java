package com.kova.dogcat.service;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import com.kova.dogcat.dto.ClassificationResult;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class ImageClassificationService {

    private OrtEnvironment env;
    private OrtSession session;

    private List<String> labels;
    private float[] mean;
    private float[] std;
    private double tau;

    @PostConstruct
    void init() throws Exception {
        env = OrtEnvironment.getEnvironment();

        try (InputStream is = getClass().getResourceAsStream("/ml/model_v0.onnx")) {
            byte[] modelBytes = is.readAllBytes();
            session = env.createSession(modelBytes, new OrtSession.SessionOptions());
        }

        labels = loadLabels();
        loadPreprocess();
        tau = loadThreshold();
    }

    public ClassificationResult classify(Path imagePath) throws Exception {
        float[] input = preprocess(imagePath);
        float[] logits = runOnnx(input);
        return postprocess(logits);
    }

    // ---------- ONNX ----------
    private float[] runOnnx(float[] input) throws OrtException {
        OnnxTensor tensor = OnnxTensor.createTensor(
                env, FloatBuffer.wrap(input), new long[]{1, 3, 224, 224}
        );

        try (tensor) {
            OrtSession.Result out = session.run(Map.of("input", tensor));
            return ((float[][]) out.get(0).getValue())[0];
        }
    }

    // ---------- PREPROCESS ----------
    private float[] preprocess(Path imagePath) throws IOException {
        BufferedImage img = ImageIO.read(imagePath.toFile());
        BufferedImage resized = resize(img, 224, 224);

        float[] input = new float[3 * 224 * 224];
        int idxR = 0;
        int idxG = 224 * 224;
        int idxB = 2 * 224 * 224;

        for (int y = 0; y < 224; y++) {
            for (int x = 0; x < 224; x++) {
                int rgb = resized.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;

                input[idxR++] = (r / 255f - mean[0]) / std[0];
                input[idxG++] = (g / 255f - mean[1]) / std[1];
                input[idxB++] = (b / 255f - mean[2]) / std[2];
            }
        }
        return input;
    }

    // ---------- POSTPROCESS ----------
    private ClassificationResult postprocess(float[] logits) {
        double[] probs = softmax(logits);
        int argmax = argmax(probs);
        double conf = probs[argmax];

        if (conf < tau) {
            return new ClassificationResult("other", conf);
        }
        return new ClassificationResult(labels.get(argmax), conf);
    }

    // ---------- UTILS ----------
    private static double[] softmax(float[] x) {
        double max = Double.NEGATIVE_INFINITY;
        for (float v : x) {
            if (v > max) max = v;
        }

        double sum = 0.0;
        double[] e = new double[x.length];

        for (int i = 0; i < x.length; i++) {
            e[i] = Math.exp(x[i] - max);
            sum += e[i];
        }

        for (int i = 0; i < e.length; i++) {
            e[i] /= sum;
        }

        return e;
    }

    private static int argmax(double[] x) {
        int idx = 0;
        for (int i = 1; i < x.length; i++)
            if (x[i] > x[idx]) idx = i;
        return idx;
    }

    private static BufferedImage resize(BufferedImage img, int w, int h) {
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = out.createGraphics();
        g.drawImage(img, 0, 0, w, h, null);
        g.dispose();
        return out;
    }

    // ---------- LOAD CONFIG ----------
    private List<String> loadLabels() throws IOException {
        try (InputStream is = getClass().getResourceAsStream("/ml/labels.json")) {
            return new ObjectMapper().readValue(is, new TypeReference<>() {});
        }
    }

    private void loadPreprocess() throws IOException {
        try (InputStream is = getClass().getResourceAsStream("/ml/preprocess.json")) {

            Objects.requireNonNull(is, "preprocess.json not found on classpath");

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(is);

            JsonNode norm = root.get("normalization");
            Objects.requireNonNull(norm, "preprocess.json: missing 'normalization'");

            JsonNode meanNode = norm.get("mean");
            JsonNode stdNode  = norm.get("std");

            Objects.requireNonNull(meanNode, "preprocess.json: missing 'normalization.mean'");
            Objects.requireNonNull(stdNode,  "preprocess.json: missing 'normalization.std'");

            mean = toFloatArray(meanNode);
            std  = toFloatArray(stdNode);
        }
    }

    private double loadThreshold() throws IOException {
        try (InputStream is = getClass().getResourceAsStream("/ml/threshold.json")) {
            return new ObjectMapper().readTree(is).get("tau").asDouble();
        }
    }

    private static float[] toFloatArray(JsonNode n) {
        float[] a = new float[n.size()];
        for (int i = 0; i < n.size(); i++) a[i] = (float) n.get(i).asDouble();
        return a;
    }
}