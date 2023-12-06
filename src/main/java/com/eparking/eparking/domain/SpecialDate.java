package com.eparking.eparking.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpecialDate {
    private int specialDateID;
    private LocalDateTime startSpecialDate;
    private LocalDateTime endSpecialDate;

}
