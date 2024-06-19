package com.prolegacy.atom2024backend.services;

import com.prolegacy.atom2024backend.common.auth.entities.id.UserId;
import com.prolegacy.atom2024backend.common.exceptions.BusinessLogicException;
import com.prolegacy.atom2024backend.entities.ids.TopicId;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.export.SimplePdfReportConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.sql.DataSource;
import java.awt.Image;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    @Value("classpath:diploma.jrxml")
    private Resource diploma;

    @Value("classpath:background.png")
    private Resource background;

    @Value("classpath:logo.png")
    private Resource logo;

    @Autowired
    private DataSource dataSource;

    public Resource createDiploma(UserId userId, TopicId topicId) {
        try {
            return jasperToPdf(List.of(createDiplomaInternal(userId, topicId)));
        } catch (Exception e) {
            throw new BusinessLogicException("Ошибка генерации печатной формы", e);
        }
    }

    private JasperPrint createDiplomaInternal(UserId userId, TopicId topicId) throws IOException, JRException, SQLException {
        InputStream inputStream = diploma.getInputStream();
        JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
        Image backgroundImage = ImageIO.read(background.getInputStream());
        Image logoImage = ImageIO.read(logo.getInputStream());
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("userId", userId.longValue());
        parameters.put("topicId", topicId.longValue());
        parameters.put("background", backgroundImage);
        parameters.put("logo", logoImage);
        try (var connection = dataSource.getConnection()) {
            return JasperFillManager.fillReport(jasperReport, parameters, connection);
        }
    }

    private Resource jasperToPdf(List<JasperPrint> prints) throws IOException, JRException {
        JRPdfExporter exporter = new JRPdfExporter();
        SimplePdfExporterConfiguration exportConfig
                = new SimplePdfExporterConfiguration();
        exportConfig.setEncrypted(true);
        exporter.setConfiguration(exportConfig);
        exporter.setExporterInput(SimpleExporterInput.getInstance(prints));
        try (var output = new ByteArrayOutputStream()) {
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(output));
            exporter.exportReport();
            return new ByteArrayResource(output.toByteArray());
        }
    }
}
