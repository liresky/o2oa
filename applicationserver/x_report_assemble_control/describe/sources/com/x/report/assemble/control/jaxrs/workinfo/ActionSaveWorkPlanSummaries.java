package com.x.report.assemble.control.jaxrs.workinfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.report.assemble.control.ExceptionWrapInConvert;
import com.x.report.assemble.control.jaxrs.workinfo.exception.ExceptionParameterInvalid;
import com.x.report.assemble.control.jaxrs.workinfo.exception.ExceptionQueryWorkInfoWithId;
import com.x.report.assemble.control.jaxrs.workinfo.exception.ExceptionSaveWorkInfo;
import com.x.report.assemble.control.jaxrs.workinfo.exception.ExceptionWorkInfoNotExists;
import com.x.report.core.entity.Report_I_WorkInfo;

public class ActionSaveWorkPlanSummaries extends BaseAction {
	
	private Logger logger = LoggerFactory.getLogger( ActionSaveWorkPlanSummaries.class );
	
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		Wi wi = null;
		Boolean check = true;
		Report_I_WorkInfo workInfo = null;
		
		try {
			wi = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionWrapInConvert( e, jsonElement );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}

		if( check ){
			if(ListTools.isNotEmpty( wi.getWorkPlanSummaryList())) {
				for( WiWorkInfo wiWorkInfo :  wi.getWorkPlanSummaryList() ) {
					if( StringUtils.isEmpty(wiWorkInfo.getId()) ){
						check = false;
						Exception exception = new ExceptionParameterInvalid( "属性'id'不允许为空！" );
						result.error( exception );
					}
//					if( StringUtils.isEmpty(wiWorkInfo.getWorkPlanSummary()) ){
//						check = false;
//						Exception exception = new ExceptionParameterInvalid( "属性'workPlanSummary'不允许为空！" );
//						result.error( exception );
//					}
					try {
						workInfo = report_I_WorkInfoServiceAdv.get( wiWorkInfo.getId() );
						if( workInfo == null ) {
							check = false;
							Exception exception = new ExceptionWorkInfoNotExists( wiWorkInfo.getId() );
							result.error( exception );
						}
					}catch( Exception e ) {
						check = false;
						Exception exception = new ExceptionQueryWorkInfoWithId( e, wiWorkInfo.getId() );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}
				}
				List<Wo> wos = new ArrayList<>();
				for( WiWorkInfo wiWorkInfo :  wi.getWorkPlanSummaryList() ) {
					try {
						workInfo = report_I_WorkInfoServiceAdv.saveWorkPlanSummary( wiWorkInfo.getId(), wiWorkInfo.getWorkPlanSummary() );
						wos.add(new Wo( workInfo.getId()));
					} catch (Exception e) {
						Exception exception = new ExceptionSaveWorkInfo( e );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}
				}
				result.setData( wos );
			}
		}
		return result;
	}
	
	public static class Wi implements Serializable{
		
		private static final long serialVersionUID = -5076990764713538973L;

		@FieldDescribe( "workPlanSummaryList" )
		private List<WiWorkInfo> workPlanSummaryList = null;

		public List<WiWorkInfo> getWorkPlanSummaryList() {
			return workPlanSummaryList;
		}

		public void setWorkPlanSummaryList(List<WiWorkInfo> workPlanSummaryList) {
			this.workPlanSummaryList = workPlanSummaryList;
		}
	}
	
public static class WiWorkInfo implements Serializable{
		
		private static final long serialVersionUID = -5076990764713538973L;

		@FieldDescribe( "workInfoId" )
		private String id = "";

		@FieldDescribe( "工作计划汇总信息" )
		private String workPlanSummary = "";

		public String getId() {
			return id;
		}

		public String getWorkPlanSummary() {
			return workPlanSummary;
		}

		public void setId(String id) {
			this.id = id;
		}

		public void setWorkPlanSummary(String workPlanSummary) {
			this.workPlanSummary = workPlanSummary;
		}		
	}
	
	public static class Wo extends WoId {
		public Wo( String id ) {
			setId( id );
		}
	}

}