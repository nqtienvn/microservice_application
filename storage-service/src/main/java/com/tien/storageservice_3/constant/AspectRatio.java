package com.tien.storageservice_3.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AspectRatio {
    RATIO_1_1("1:1"),
    RATIO_4_3("4:3"),
    RATIO_3_2("3:2"),
    RATIO_16_9("16:9"),
    RATIO_21_9("21:9"),
    RATIO_9_16("9:16"),
    RATIO_2_3("2:3"),
    RATIO_5_4("5:4"),
    RATIO_7_5("7:5"),
    RATIO_3_4("3:4");
    private final String value;
}
