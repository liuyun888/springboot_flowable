package com.boot.flowable.entity;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class RollBackVo {
   //act_ru_task 表中
   public String executionId;

   public List<String> targetTaskDefKeys;


}
