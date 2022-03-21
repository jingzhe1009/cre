package com.bonc.frame.module.db.meta.core;

import com.bonc.frame.module.db.meta.core.exception.StartTaskFailException;

public interface IScanService {

	public void startScan() throws StartTaskFailException;
}
