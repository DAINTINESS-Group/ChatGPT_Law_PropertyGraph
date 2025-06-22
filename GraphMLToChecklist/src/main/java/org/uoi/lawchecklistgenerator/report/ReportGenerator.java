package org.uoi.lawchecklistgenerator.report;

import org.uoi.lawchecklistgenerator.engine.GraphEngine;

import java.nio.file.Path;

public interface ReportGenerator {

    void generateReport(GraphEngine engine, Path out) throws Exception;
}
