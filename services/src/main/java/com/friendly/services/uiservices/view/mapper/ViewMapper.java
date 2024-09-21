package com.friendly.services.uiservices.view.mapper;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.view.AbstractView;
import com.friendly.commons.models.view.ConditionItem;
import com.friendly.commons.models.view.FrameView;
import com.friendly.commons.models.view.GroupUpdateView;
import com.friendly.commons.models.view.ListView;
import com.friendly.commons.models.view.SearchView;
import com.friendly.commons.models.view.ViewColumn;
import com.friendly.commons.models.view.ViewCondition;
import com.friendly.commons.models.view.ViewSimple;
import com.friendly.services.device.info.utils.DeviceUpdateColumn;
import com.friendly.services.device.info.utils.DeviceViewUtil;
import com.friendly.services.uiservices.view.orm.iotw.model.ColumnConditionEntity;
import com.friendly.services.uiservices.view.orm.iotw.model.ColumnEntity;
import com.friendly.services.uiservices.view.orm.iotw.model.ConditionItemEntity;
import com.friendly.services.uiservices.frame.orm.iotw.model.FrameConditionEntity;
import com.friendly.services.uiservices.view.orm.iotw.model.ViewEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ViewMapper {

    public ListView viewEntityToListView(final ViewEntity viewEntity, boolean isDefaultUser) {
        return ListView.builder()
                       .type(viewEntity.getType())
                       .id(viewEntity.getId())
                       .name(viewEntity.getName())
                       .isDefaultUser(isDefaultUser)
                       .isDefaultPublic(viewEntity.getDefaultDomain())
                       .build();
    }

    public SearchView viewEntityToSearchView(final ViewEntity viewEntity, boolean isDefaultUser) {
        return SearchView.builder()
                         .type(viewEntity.getType())
                         .id(viewEntity.getId())
                         .name(viewEntity.getName())
                         .isDefaultUser(isDefaultUser)
                         .isDefaultPublic(viewEntity.getDefaultDomain())
                         .build();
    }

    public FrameView viewEntityToFrameView(final ViewEntity viewEntity, boolean isDefaultUser) {
        return FrameView.builder()
                .type(viewEntity.getType())
                .id(viewEntity.getId())
                .name(viewEntity.getName())
                .isDefaultUser(isDefaultUser)
                .isDefaultPublic(viewEntity.getDefaultDomain())
                .tableWidth(viewEntity.getTableWidth())
                .build();
    }

    public FrameView viewEntityToFrameView(final ViewEntity viewEntity) {
        return FrameView.builder()
                        .type(viewEntity.getType())
                        .id(viewEntity.getId())
                        .name(viewEntity.getName())
                        .isDefaultUser(viewEntity.getDefaultUser())
                        .isDefaultPublic(viewEntity.getDefaultDomain())
                        .tableWidth(viewEntity.getTableWidth())
                        .build();
    }

    public GroupUpdateView viewEntityToGroupUpdateView(final ViewEntity view) {
        return GroupUpdateView.builder()
                .type(view.getType())
                .id(view.getId())
                .name(view.getName())
                .isDefaultPublic(view.getDefaultUser())
                .isDefaultPublic(view.getDefaultDomain())
                .build();

    }

    public ViewEntity viewToViewEntity(final AbstractView view, final ClientType clientType, final Integer domainId) {
        return ViewEntity.builder()
                         .type(view.getType())
                         .id(view.getId())
                         .name(view.getName())
                         .defaultUser(view.isDefaultUser())
                         .defaultDomain(view.isDefaultPublic())
                         .clientType(clientType)
                         .domainId(domainId)
                         .build();
    }

    public List<ColumnEntity> columnsToColumnEntities(final List<ViewColumn> columns, final Long viewId) {
        if (columns == null) {
            return Collections.emptyList();
        }
        return columns.stream()
                      .map(c -> columnToColumnEntity(c, viewId))
                      .collect(Collectors.toList());
    }

    public ColumnEntity columnToColumnEntity(final ViewColumn column, final Long viewId) {
        if (column == null) {
            return null;
        }
        return ColumnEntity.builder()
                           .viewId(viewId)
                           .columnKey(column.getColumnKey())
                           .visibleIndex(column.getIndexVisible())
                           .orderIndex(column.getIndexSort())
                           .direction(column.getDirection())
                           .build();
    }

    public ColumnConditionEntity conditionToConditionEntity(final ViewCondition condition, final Long viewId,
                                                            final Long parentId) {
        if (condition == null) {
            return null;
        }
        return ColumnConditionEntity.builder()
                                    .id(condition.getId())
                                    .parentId(parentId)
                                    .viewId(viewId)
                                    .columnKey(condition.getColumnKey())
                                    .logic(condition.getLogic())
                                    .type(condition.getCompare())
                                    .stringValue(condition.getConditionString())
                                    .dateValue(condition.getConditionDateIso())
                                    .build();
    }

    public List<ViewSimple> fieldsToViewSimples(final List<Object[]> simpleViews) {
        return simpleViews.stream()
                .map(v -> ViewSimple.builder()
                        .id((Long) v[0])
                        .name((String) v[1])
                        .isDefaultUser((Boolean) v[2])
                        .isDefaultPublic((Boolean) v[3])
                        .build())
                .collect(Collectors.toList());
    }

    public ViewSimple fieldsToViewSimples(final Object[] simpleView, boolean isDefaultUser,
                                          Boolean devicePriority) {

        return ViewSimple.builder()
                .id((Long) simpleView[0])
                .name((String) simpleView[1])
                .isDefaultUser(isDefaultUser)
                .isDefaultPublic((Boolean) simpleView[3])
                .isDevicePriority(devicePriority)
                .build();
    }

    public ViewColumn columnEntityToColumn(final ColumnEntity columnEntity, final String localeId) {
        return ViewColumn.builder()
                         .columnKey(columnEntity.getColumnKey())
                         .columnName(DeviceViewUtil.getColumnName(columnEntity.getColumnKey(), localeId))
                         .canSort(DeviceUpdateColumn.canSort(columnEntity.getColumnKey()))
                         .indexVisible(columnEntity.getVisibleIndex())
                         .indexSort(columnEntity.getOrderIndex())
                         .direction(columnEntity.getDirection())
                         .build();
    }

    public ConditionItem conditionItemEntityToConditionItem(ConditionItemEntity entity) {
        return ConditionItem.builder()
                .viewId(entity.getViewId())
                .viewIndex(entity.getViewIndex())
                .viewName(entity.getViewName())
                .build();
    }

    public FrameConditionEntity createFrameConditionEntity(ViewCondition condition, Long viewId, Long parentId) {
        return FrameConditionEntity.builder()
                .viewId(viewId)
                .parentId(parentId)
                .columnKey(condition.getColumnKey())
                .compare(condition.getCompare())
                .logic(condition.getLogic())
                .stringValue(condition.getConditionString())
                .dateValue(condition.getConditionDateIso())
                .build();
    }
}
