package com.yangzhou.frame.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yangzhou.aop.logging.SysLogAnnotation;
import com.yangzhou.enums.StatusCode;
import com.yangzhou.frame.domain.SysLog;
import com.yangzhou.frame.service.SysLogService;
import com.yangzhou.util.MsgInfo;

@RestController
@RequestMapping("/api/v1/syslog")
public class SysLogController {

	@Autowired
	private SysLogService sysLogService;

    @GetMapping("/queryPage")
    @SysLogAnnotation(value="系统日志列表")
	public ResponseEntity<MsgInfo<Page<SysLog>>> querySysLogsPageList(
			@RequestParam(value = "currentPage", defaultValue = "0") Long currentPage
    		,@RequestParam(value ="pageSize", defaultValue = "5")Long pageSize
    		,@RequestParam(value ="module", required=false)String module
    		,@RequestParam(value ="startTime", required=false)String startTime
    		,@RequestParam(value ="endTime", required=false)String endTime) {  
    	Page<SysLog> page = new Page<>(currentPage, pageSize);
    	page = sysLogService.querySysLogsPageList(page, module, startTime, endTime);
    	return ResponseEntity.ok(new MsgInfo(StatusCode.ok.value, StatusCode.ok.reasonPhrase, page));
    }

  @GetMapping("/queryTest")
	public ResponseEntity<MsgInfo<List<SysLog>>> queryTest() {
    return MsgInfo.ok(sysLogService.queryTest());
  }
}
