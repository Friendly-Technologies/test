package com.friendly.services.device.parameterstree.utils.supplier;

import com.friendly.commons.models.device.ProtocolType;
import com.friendly.commons.models.tree.TreeObject;
import com.friendly.commons.models.tree.TreeParameter;
import com.friendly.services.device.parameterstree.utils.ParameterUtil;
import java.util.Iterator;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public abstract class AbstractTreeSupplier<
        TreeParam extends TreeParameter,
        TreeObj extends TreeObject<TreeObj, TreeParam>> {

    public TreeObj createTreeObj(String fullName, TreeObj parentObj, ProtocolType protocol) {
        TreeObj treeObj = createTreeObj();
        treeObj.setFullName(fullName);
        treeObj.setShortName(ParameterUtil.getShortName(fullName));
        treeObj.setParent(parentObj);
        return treeObj;
    }

    public TreeParam createTreeParam(String fullName, TreeObj parentObject) {
        TreeParam treeParam = createTreeParam();
        treeParam.setFullName(fullName);
        treeParam.setShortName(ParameterUtil.getShortName(fullName));
        return treeParam;
    }

    public void init() {}
    protected abstract TreeObj createTreeObj();
    protected abstract TreeParam createTreeParam();

    public boolean isObjectValid(String name) {
        return true;
    }

    public String processNotValidObject(String fullName, TreeObj parentObject, Iterator<String> it) {
        return null;
    }

    public void postObjectWalk(TreeObj deviceObject) {

    }
}