package com.yangzhou.frame.domain;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yangzhou.domain.BaseEntityWithID;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class SysLog extends BaseEntityWithID implements Serializable{
	private static final long serialVersionUID = 7839048172163472091L;
	private String ip;//访问用户的IP地址
	private String module;//被访问的模块
	private String action;//操作的内容
	private String operator;//操作者，当前登录用户的用户名。
	@TableField("operate_at")
	@JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	private Date operateAt;//操作时间
	
	
	

}
