// RevenueByDate.java
package com.project.tmartweb.application.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.sql.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RevenueByDate {
    private Date date;
    private Double totalMoney;
}