package com.yangzhou.frame.service;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yangzhou.frame.domain.SysLog;
import com.yangzhou.frame.mapper.SysLogMapper;
import com.yangzhou.util.IpUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Service class for managing users.
 */
@Slf4j
@Service
@Transactional
public class SysLogService {

    @Autowired
    private SysLogMapper sysLogMapper;

    private static Object obj = new Object();

    @Async
    public void sysAddLog(String title, String message, String userLogin, HttpServletRequest request) {
        synchronized (obj) {
            final SysLog sysLog = new SysLog();
            sysLog.setIp(IpUtil.getIpAddr(request));
            sysLog.setModule(title);
            sysLog.setAction(message);
            sysLog.setOperator(userLogin);
            sysLogMapper.insert(sysLog);
        }
    }

    public Page<SysLog> querySysLogsPageList(Page<SysLog> page, String module, String startTime, String endTime) {
        final QueryWrapper<SysLog> query = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(module)) {
            query.like("module", module);
        }
        if (StringUtils.isNotEmpty(startTime)) {
            query.ge("operate_at", startTime);
        }
        if (StringUtils.isNotEmpty(endTime)) {
            query.le("operate_at", endTime);
        }
        query.orderByDesc("operate_at");
        return (Page<SysLog>) sysLogMapper.selectPage(page, query);
    }

  public List<SysLog> queryTest() {
    return sysLogMapper.selectTest();
  }

}
