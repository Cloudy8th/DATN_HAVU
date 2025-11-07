// RevenueByWeek.java
package com.project.tmartweb.application.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RevenueByWeek {
    private Integer year;
    private Integer week;
    private Double totalMoney;
}