package com.jobcard.demo.bean;

import org.apache.commons.lang3.StringUtils;

public class DefaultTemplateBean {
    //来访日期		
    private String visitDate;
    //被访人ID		
    private String intervieweesId;
    //被访人姓名		
    private String intervieweesName;
    private String interviewees;
    //事由		
    private String visitReason;
    //事由	
    private String visitReason1;
    //人员ID	
    private String userId;
    //姓名	
    private String name;
    //单位名称	
    private String company;
    //部门	
    private String depart;
    //访客类型	
    private String visitorTypeCls;

    //楼宇名称
    private String buildingName;

    //楼层名称
    private String floorNames;

    public String getVisitDate() {
        return StringUtils.isNotBlank(visitDate) ? visitDate : "";
    }

    public void setVisitDate(String visitDate) {
        this.visitDate = visitDate;
    }

    public String getIntervieweesId() {
        return StringUtils.isNotBlank(intervieweesId) ? intervieweesId : "";
    }

    public void setIntervieweesId(String intervieweesId) {
        this.intervieweesId = intervieweesId;
    }

    public String getIntervieweesName() {
        if (StringUtils.isNotBlank(intervieweesName)) {
            if (intervieweesName.length() > 4) {
                return intervieweesName.substring(0, 3) + "...";
            }
            return intervieweesName;
        }
        return "";
    }

    public void setIntervieweesName(String intervieweesName) {
        this.intervieweesName = intervieweesName;
    }

    public String getInterviewees() {
        return String.join("-", getIntervieweesId(), getIntervieweesName());
    }

    public void setInterviewees(String interviewees) {
        this.interviewees = interviewees;
    }

    public String getVisitReason() {
        if (StringUtils.isBlank(visitReason)) {
            return "";
        }
        return visitReason.length() > 8 ? visitReason.substring(0, 8) : visitReason;
    }

    public void setVisitReason(String visitReason) {
        this.visitReason = visitReason;
    }

    public String getVisitReason1() {
        if (StringUtils.isNotBlank(visitReason) && visitReason.length() > 8) {
            String sub = visitReason.substring(8);
            if (sub.length() > 8) {
                return sub.substring(0, 7) + "...";
            }
            return sub;
        }
        return "";
    }

    public void setVisitReason1(String visitReason1) {
        this.visitReason1 = visitReason1;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        if (StringUtils.isNotBlank(name)) {
            if (name.length() > 4) {
                return name.substring(0, 3) + "...";
            }
            return name;
        }
        return "";
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompany() {
        if (StringUtils.isNotBlank(company)) {
            if (company.length() > 10) {
                return company.substring(0, 9) + "...";
            }
            return company;
        }
        return "";
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getDepart() {
        return depart;
    }

    public void setDepart(String depart) {
        this.depart = depart;
    }

    public String getVisitorTypeCls() {
        return visitorTypeCls;
    }

    public void setVisitorTypeCls(String visitorTypeCls) {
        this.visitorTypeCls = visitorTypeCls;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public String getFloorNames() {
        return StringUtils.isNotBlank(floorNames) && floorNames.length() > 20 ? floorNames.substring(0,20):floorNames;
    }

    public void setFloorNames(String floorNames) {
        this.floorNames = floorNames;
    }

    public void check() {
        if (StringUtils.isBlank(userId)) {
            throw new RuntimeException("userId 不能为空");
        }
    }
}
