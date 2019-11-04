package com.vike.agent.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author: lsl
 * @createDate: 2019/11/4
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class SummaryVo {
    private long balance;
    private long todayAmount;
    private long todayBonus;
    private long lastDayAmount;
    private long lastDayBonus;
    private long monthAmount;
    private long monthBonus;
    private long lastMonthAmount;
    private long lastMonthBonus;
    private long totalAmount;
    private long totalBonus;
}
