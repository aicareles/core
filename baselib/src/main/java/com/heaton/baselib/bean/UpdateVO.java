package com.heaton.baselib.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * 更新实体类
 */
public class UpdateVO implements Serializable {
	public long   app_id;
	public String update_type;
	public String app_version;
	public int    app_version_number;
	public String app_url;
	public String app_plugin_url;
	public float  app_size;
	public String app_update;
	public String app_modified;
}
