package com.zach.sqlautomodel;

/**
 * Abstract model representing an SQL table.
 *
 * @author zach-bright
 */
public abstract class AbstractModel {
    public String generatedFrom;

    /**
     * AbstractModel constructor.
     *
     * @author zach-bright
     * @param generatedFrom String Original generator string.
     */
    AbstractModel(String generatedFrom) {
        this.generatedFrom = generatedFrom;
    }
}
