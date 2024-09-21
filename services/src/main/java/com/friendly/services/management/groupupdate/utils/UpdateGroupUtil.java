package com.friendly.services.management.groupupdate.utils;

import com.cronutils.builder.CronBuilder;
import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.field.CronField;
import com.cronutils.model.field.CronFieldName;
import com.cronutils.model.field.expression.And;
import com.cronutils.model.field.expression.Every;
import com.cronutils.model.field.expression.FieldExpression;
import com.cronutils.model.field.expression.FieldExpressionFactory;
import com.cronutils.model.field.expression.On;
import com.cronutils.parser.CronParser;
import com.friendly.services.management.groupupdate.dto.GroupUpdateReactivation;
import com.friendly.services.management.groupupdate.dto.enums.TimeIntervalEnum;
import com.friendly.services.management.groupupdate.dto.enums.WeekDaysEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class UpdateGroupUtil {
    public static GroupUpdateReactivation.GroupUpdateReactivationBuilder cronExpressionToGroupUpdateReactivation(
            String expression,
            GroupUpdateReactivation.GroupUpdateReactivationBuilder builder) {
        TimeIntervalEnum type = TimeIntervalEnum.None;
        if (!StringUtils.hasText(expression)) {
            return builder.type(type);
        }

        List<WeekDaysEnum> repeatOn = null;
        Every every = null;

        CronDefinition cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ);
        CronParser cronParser = new CronParser(cronDefinition);
        Cron cron = cronParser.parse(expression);

        log.info("Parsing expression: " + expression);

        Map<CronFieldName, CronField> map = cron.retrieveFieldsAsMap();
        CronField field = map.get(CronFieldName.MINUTE);
        FieldExpression expr = field.getExpression();
        if (expr instanceof Every) {
            every = (Every) expr;
            type = TimeIntervalEnum.Minutely;
        } else {
            field = map.get(CronFieldName.HOUR);
            expr = field.getExpression();
            if (expr instanceof Every) {
                every = (Every) expr;
                type = TimeIntervalEnum.Hourly;
            } else {
                field = map.get(CronFieldName.DAY_OF_MONTH);
                expr = field.getExpression();
                if (expr instanceof Every) {
                    every = (Every) expr;
                    type = TimeIntervalEnum.Daily;
                } else {
                    field = map.get(CronFieldName.DAY_OF_WEEK);
                    expr = field.getExpression();
                    if (expr instanceof And || expr instanceof On) {
                        type = TimeIntervalEnum.Weekly;
                        repeatOn = new ArrayList<>();
                        if (expr instanceof On) {
                            On on = (On) expr;
                            repeatOn.add(WeekDaysEnum.fromInt(on.getNth().getValue()));
                        } else {
                            And and = (And) expr;
                            repeatOn.addAll((and.getExpressions()
                                    .stream()
                                    .map(e ->
                                            WeekDaysEnum.fromInt(
                                                    ((On) e).getTime().getValue())).collect(Collectors.toList())));
                        }
                    } else {
                        field = map.get(CronFieldName.MONTH);
                        expr = field.getExpression();
                        if (expr instanceof Every) {
                            every = (Every) expr;
                            type = TimeIntervalEnum.Monthly;
                        } else {
                            type = TimeIntervalEnum.Yearly;
                        }
                    }
                }
            }
        }
        return builder.type(type)
                .repeatEvery(every == null ? null : every.getPeriod().getValue())
                .repeatOn(repeatOn);
    }

    public static String reactivationDetailsToCron(GroupUpdateReactivation reactivation, Instant startOn) {
        ZonedDateTime start = startOn.atZone(ZoneId.systemDefault());
        CronBuilder builder = CronBuilder.cron(CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ))
                .withSecond(FieldExpressionFactory.on(0))
                .withMinute(FieldExpressionFactory.on(0))
                .withHour(FieldExpressionFactory.on(start.getHour()))
                .withYear(FieldExpression.always());
        log.info("Converting: " + reactivation);

        switch (reactivation.getType()) {
            case None:
                return "";
            case Minutely:
                builder.withMinute(FieldExpressionFactory.every(reactivation.getRepeatEvery()));
                builder.withHour(FieldExpression.always());
                builder.withDoM(FieldExpression.always());
                builder.withMonth(FieldExpression.always());
                builder.withDoW(FieldExpression.questionMark());
                break;
            case Hourly:
                builder.withHour(FieldExpressionFactory.every(
                        FieldExpressionFactory.on(start.getHour()), reactivation.getRepeatEvery()));
                builder.withDoM(FieldExpression.always());
                builder.withMonth(FieldExpression.always());
                builder.withDoW(FieldExpression.questionMark());
                break;
            case Daily:
                builder.withDoM(FieldExpressionFactory.every(
                        FieldExpressionFactory.on(1), reactivation.getRepeatEvery()));
                builder.withMonth(FieldExpression.always());
                builder.withDoW(FieldExpression.questionMark());
                break;
            case Weekly:
                builder.withDoM(FieldExpression.questionMark());
                builder.withMonth(FieldExpression.always());
                builder.withDoW(FieldExpressionFactory.and(reactivation.getRepeatOn().stream()
                        .map(e -> FieldExpressionFactory.on(e.ordinal())).collect(Collectors.toList())));

                break;
            case Monthly:
                builder.withDoM(FieldExpressionFactory.on(start.getMonth().getValue()));

                builder.withMonth(FieldExpressionFactory.every(
                        FieldExpressionFactory.on(start.getMonth().getValue()), reactivation.getRepeatEvery()));
                builder.withDoW(FieldExpression.questionMark());
                break;
            case Yearly:
                builder.withDoM(FieldExpressionFactory.on(start.getDayOfMonth()));
                builder.withMonth(FieldExpressionFactory.on(start.getMonth().getValue()));
                builder.withDoW(FieldExpression.questionMark());
                builder.withYear(FieldExpression.questionMark());
                break;
        }


        Cron cron = builder.instance();
        String expr = cron.asString(); // 0 * * L-3 * ? *

        log.info("Result expression: " + expr);
        return expr;
    }
}