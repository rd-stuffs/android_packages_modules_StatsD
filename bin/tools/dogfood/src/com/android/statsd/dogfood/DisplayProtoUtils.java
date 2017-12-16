/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.statsd.dogfood;

import android.text.format.DateFormat;

import com.android.os.StatsLog;

import java.util.List;

public class DisplayProtoUtils {
    public static void displayLogReport(StringBuilder sb, StatsLog.ConfigMetricsReportList reports) {
        sb.append("ConfigKey: ");
        if (reports.hasConfigKey()) {
            com.android.os.StatsLog.ConfigMetricsReportList.ConfigKey key = reports.getConfigKey();
            sb.append("\tuid: ").append(key.getUid()).append(" name: ").append(key.getName())
                    .append("\n");
        }

        for (StatsLog.ConfigMetricsReport report : reports.getReportsList()) {
            sb.append("StatsLogReport size: ").append(report.getMetricsCount()).append("\n");
            for (StatsLog.StatsLogReport log : report.getMetricsList()) {
                sb.append("\n\n");
                sb.append("metric id: ").append(log.getMetricName()).append("\n");
                sb.append("start time:").append(getDateStr(log.getStartReportNanos())).append("\n");
                sb.append("end time:").append(getDateStr(log.getEndReportNanos())).append("\n");

                switch (log.getDataCase()) {
                    case DURATION_METRICS:
                        sb.append("Duration metric data\n");
                        displayDurationMetricData(sb, log);
                        break;
                    case EVENT_METRICS:
                        sb.append("Event metric data\n");
                        displayEventMetricData(sb, log);
                        break;
                    case COUNT_METRICS:
                        sb.append("Count metric data\n");
                        displayCountMetricData(sb, log);
                        break;
                    case GAUGE_METRICS:
                        sb.append("Gauge metric data\n");
                        displayGaugeMetricData(sb, log);
                        break;
                    case VALUE_METRICS:
                        sb.append("Value metric data\n");
                        displayValueMetricData(sb, log);
                        break;
                    case DATA_NOT_SET:
                        sb.append("No metric data\n");
                        break;
                }
            }
        }
    }

    public static String getDateStr(long nanoSec) {
        return DateFormat.format("dd/MM hh:mm:ss", nanoSec/1000000).toString();
    }

    private static void displayDimension(StringBuilder sb, List<StatsLog.KeyValuePair> pairs) {
        for (com.android.os.StatsLog.KeyValuePair kv : pairs) {
            sb.append(kv.getKey()).append(":");
            if (kv.hasValueBool()) {
                sb.append(kv.getValueBool());
            } else if (kv.hasValueFloat()) {
                sb.append(kv.getValueFloat());
            } else if (kv.hasValueInt()) {
                sb.append(kv.getValueInt());
            } else if (kv.hasValueStr()) {
                sb.append(kv.getValueStr());
            }
            sb.append(" ");
        }
    }

    public static void displayDurationMetricData(StringBuilder sb, StatsLog.StatsLogReport log) {
        StatsLog.StatsLogReport.DurationMetricDataWrapper durationMetricDataWrapper
                = log.getDurationMetrics();
        sb.append("Dimension size: ").append(durationMetricDataWrapper.getDataCount()).append("\n");
        for (StatsLog.DurationMetricData duration : durationMetricDataWrapper.getDataList()) {
            sb.append("dimension: ");
            displayDimension(sb, duration.getDimensionList());
            sb.append("\n");

            for (StatsLog.DurationBucketInfo info : duration.getBucketInfoList())  {
                sb.append("\t[").append(getDateStr(info.getStartBucketNanos())).append("-")
                        .append(getDateStr(info.getEndBucketNanos())).append("] -> ")
                        .append(info.getDurationNanos()).append(" ns\n");
            }
        }
    }

    public static void displayEventMetricData(StringBuilder sb, StatsLog.StatsLogReport log) {
        sb.append("Contains ").append(log.getEventMetrics().getDataCount()).append(" events\n");
        StatsLog.StatsLogReport.EventMetricDataWrapper eventMetricDataWrapper =
                log.getEventMetrics();
        for (StatsLog.EventMetricData event : eventMetricDataWrapper.getDataList()) {
            sb.append(getDateStr(event.getTimestampNanos())).append(": ");
            sb.append(event.getAtom().getPushedCase().toString()).append("\n");
        }
    }

    public static void displayCountMetricData(StringBuilder sb, StatsLog.StatsLogReport log) {
        StatsLog.StatsLogReport.CountMetricDataWrapper countMetricDataWrapper
                = log.getCountMetrics();
        sb.append("Dimension size: ").append(countMetricDataWrapper.getDataCount()).append("\n");
        for (StatsLog.CountMetricData count : countMetricDataWrapper.getDataList()) {
            sb.append("dimension: ");
            displayDimension(sb, count.getDimensionList());
            sb.append("\n");

            for (StatsLog.CountBucketInfo info : count.getBucketInfoList())  {
                sb.append("\t[").append(getDateStr(info.getStartBucketNanos())).append("-")
                        .append(getDateStr(info.getEndBucketNanos())).append("] -> ")
                        .append(info.getCount()).append("\n");
            }
        }
    }

    public static void displayGaugeMetricData(StringBuilder sb, StatsLog.StatsLogReport log) {
        sb.append("Display me!");
    }

    public static void displayValueMetricData(StringBuilder sb, StatsLog.StatsLogReport log) {
        sb.append("Display me!");
    }
}