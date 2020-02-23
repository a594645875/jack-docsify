```java
//今天
LocalDate today = LocalDate.now();

//上一个周一
LocalDate lastMonday = today.with(TemporalAdjusters.previous(DayOfWeek.MONDAY));

//下一个周日
LocalDate nextSunday = today.with(TemporalAdjusters.next(DayOfWeek.SUNDAY));
```