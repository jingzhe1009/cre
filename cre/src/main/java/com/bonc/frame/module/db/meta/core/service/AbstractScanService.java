package com.bonc.frame.module.db.meta.core.service;

import com.bonc.frame.module.db.meta.core.IScanService;
import com.bonc.frame.module.db.meta.core.exception.AfterScanException;
import com.bonc.frame.module.db.meta.core.exception.BeforeScanException;
import com.bonc.frame.module.db.meta.core.exception.ScanException;
import com.bonc.frame.module.db.meta.core.exception.StartTaskFailException;
import com.bonc.frame.module.db.meta.core.model.ScanTask;
import com.bonc.frame.module.db.meta.core.model.ScanTaskLog;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.TimerTask;


public abstract class AbstractScanService<T> extends TimerTask implements IScanService {

	private static Log log = LogFactory.getLog(AbstractScanService.class);
	
	private static StatusEnum status = StatusEnum.ready;
	protected ScanTask scanTask;
	protected ScanTaskLog scanTaskLog;
	private static final long delay  = 500;//延迟三秒钟
	
	public AbstractScanService(ScanTask scanTask) {
		this.scanTask = scanTask;
		scanTaskLog  = new ScanTaskLog();
		scanTaskLog.setTaskId(scanTask.getTaskId());
	}

    @Override
    public void startScan() throws StartTaskFailException {
		if (status == StatusEnum.scanning) {
            StringBuffer sb = new StringBuffer();
            sb.append("任务正在进行中！请稍后再试");
            this.scanTaskLog.setTaskDetail(sb.toString());
            makeLog();
            throw new StartTaskFailException(sb.toString());
        }
        status = StatusEnum.scanning;
//		TaskEngine.getInstance().schedule(this, delay);//启动任务
        run();
    }

    public abstract void beforeScan() throws BeforeScanException;
	public abstract void scan() throws ScanException;
	public abstract void afterScan() throws AfterScanException;
	public abstract void makeLog();

    @Override
	public void run(){
		StringBuffer sb = new StringBuffer();
		try {
			beforeScan();
			scan();
			afterScan();
		} catch (BeforeScanException e) {
			sb.append(e.getClass()).append("--").append(e.getMessage());
			this.scanTaskLog.setTaskDetail(sb.toString());
		} catch (ScanException e) {
			sb.append(e.getClass()).append("--").append(e.getMessage());
			this.scanTaskLog.setTaskDetail(sb.toString());
		} catch (AfterScanException e) {
			sb.append(e.getClass()).append("--").append(e.getMessage());
			this.scanTaskLog.setTaskDetail(sb.toString());
		} 
		
		try{
			if(sb.length()>50000){
				scanTaskLog.setTaskDetail(sb.substring(0,50000)+"...");
			}
			makeLog();
			status = StatusEnum.stop;
		}catch(Exception e){
			log.error(e);
			status = StatusEnum.exception;
		}
		
	}
	
	public enum StatusEnum {
		ready, scanning, stop, exception;
    }
}
