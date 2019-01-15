package com.test.jimware.vo;

import java.io.Serializable;
import java.util.List;

/**
 * 分页数据库对象类
 *
 */
public class Pager<T> implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = -7927282174860802382L;

	// 排序方式
	public enum OrderType {
		ASC, DESC
	}
	// 分页方式
	public enum ExportType {
		EXPORT, QUERY
	}
	public static final Integer MAX_PAGE_SIZE = 100;// 每页最大记录数限制
	public static final Integer MAX_EXPORT_PAGE_SIZE = 5000;// 每页最大记录数限制
	private Integer pageNumber = 1;// 当前页码
	private Integer startNum = 1;// 当前页码
	private Integer endNum = 1;// 当前页码


	private Integer pageSize =10;// 每页记录数

	private Integer totalCount = 0;// 总记录数

	private Integer pageCount = 0;// 总页数

	private String property;// 查找属性名称

	private String keyword;// 查找关键字

	private String orderBy = "";// 排序字段

	private OrderType orderType = OrderType.DESC;// 排序方式

	//分页方式
	private ExportType exportType =ExportType.QUERY;
	private List<T> list;// 数据List

	private QueryCondition condition;

	public Pager() {
	}

	public Pager(List<T> items, int totalRecords, int pageNo) {
		setList(items);
		setTotalCount(totalRecords);
		setPageNumber(pageNo);
	}

	/**
	 * 获取当前页码
	 *
	 * @return 当前页码
	 */
	public Integer getPageNumber() {
		return pageNumber;
	}

	/**
	 * 设置当前页码
	 *
	 * @param pageNumber
	 *            当前页码
	 */
	public void setPageNumber(Integer pageNumber) {
		if (pageNumber == null || pageNumber < 1) {
			pageNumber = 1;
		}
		this.pageNumber = pageNumber;
	}

	/**
	 * 获取每页记录数，默认为20条
	 *
	 * @return 每页记录数
	 */
	public Integer getPageSize() {
		return pageSize;
	}

	/**
	 * 设置每页记录数，最大不超过100条
	 *
	 * @param pageSize
	 *            每页记录数
	 */
	public void setPageSize(Integer pageSize) {
		if (pageSize < 1) {
			pageSize = 1;
		}
		if(ExportType.QUERY.name().equals(getExportType())){
			if (pageSize > MAX_PAGE_SIZE) {
				pageSize = MAX_PAGE_SIZE;
			}
		}else {
			if (pageSize > MAX_EXPORT_PAGE_SIZE) {
				pageSize = MAX_EXPORT_PAGE_SIZE;
			}
		}
		this.pageSize = pageSize;
	}

	/**
	 * 获取总记录数
	 *
	 * @return 总记录数
	 */
	public Integer getTotalCount() {
		return totalCount;
	}

	/**
	 * 设置总记录数
	 *
	 * @param totalCount
	 *            总记录数
	 */
	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}

	/**
	 * 获取总页数
	 *
	 * @return 总页数
	 */
	public Integer getPageCount() {
		pageCount = totalCount / pageSize;
		if (totalCount % pageSize > 0) {
			pageCount++;
		}
		return pageCount;
	}

	/**
	 * 设置总页数
	 *
	 * @param pageCount
	 *            总页数
	 */
	public void setPageCount(Integer pageCount) {
		this.pageCount = pageCount;
	}

	/**
	 * 获取查找属性名称
	 *
	 * @return 查找属性名称
	 */
	public String getProperty() {
		return property;
	}

	/**
	 * 设置查找属性名称
	 *
	 * @param property
	 *            查找属性名称
	 */
	public void setProperty(String property) {
		this.property = property;
	}

	/**
	 * 获取查找关键字，默认为空串
	 *
	 * @return 查找关键字
	 */
	public String getKeyword() {
		return keyword;
	}

	/**
	 * 设置查找关键字
	 *
	 * @param keyword
	 *            查找关键字
	 */
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	/**
	 * 获取排序字段
	 *
	 * @return 排序字段
	 */
	public String getOrderBy() {
		return orderBy;
	}

	/**
	 * 设置排序字段
	 *
	 * @param orderBy
	 *            排序字段
	 */
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	/**
	 * 获取排序方式，默认desc
	 *
	 * @return 排序方式
	 */
	public String getOrderType() {
		return orderType.name();
	}

	/**
	 * 设置分页方式
	 *
	 * @param orderType
	 *            分页方式
	 */
	public void setOrderType(OrderType orderType) {
		this.orderType = orderType;
	}

	public String getExportType() {
		return exportType.name();
	}

	public void setExportType(ExportType exportType) {
		this.exportType = exportType;
	}

	/**
	 * 获取数据列表
	 *
	 * @return 数据list
	 */
	public List<T> getList() {
		return list;
	}

	/**
	 * 设置数据list
	 *
	 * @param list
	 *            数据list
	 */
	public void setList(List<T> list) {
		this.list = list;
	}

	/**
	 * 是否还有下一页.
	 *
	 * @return 布尔值
	 */
	public boolean isHasNext() {
		return pageNumber + 1 <= getPageCount();
	}

	/**
	 * 取得下页的页号, 序号从1开始. 当前页为尾页时仍返回尾页序号.
	 *
	 * @return 下一页码
	 */
	public int getNextPage() {
		if (isHasNext()) {
			return pageNumber + 1;
		} else {
			return pageNumber;
		}
	}

	/**
	 * 是否还有上一页.
	 *
	 * @return 布尔值
	 */
	public boolean isHasPre() {
		return pageNumber - 1 >= 1;
	}

	/**
	 * 取得上页的页号, 序号从1开始. 当前页为首页时返回首页序号.
	 *
	 * @return 上一页码
	 */
	public int getPrePage() {
		if (isHasPre()) {
			return pageNumber - 1;
		} else {
			return pageNumber;
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Pager [pageNumber=");
		builder.append(pageNumber);
		builder.append(", pageSize=");
		builder.append(pageSize);
		builder.append(", totalCount=");
		builder.append(totalCount);
		builder.append(", pageCount=");
		builder.append(pageCount);
		builder.append(", property=");
		builder.append(property);
		builder.append(", keyword=");
		builder.append(keyword);
		builder.append(", orderBy=");
		builder.append(orderBy);
		builder.append(", orderType=");
		builder.append(orderType);
		builder.append(", list=");
		builder.append(list != null ? list.size() : null);
		builder.append("]");
		return builder.toString();
	}

	public QueryCondition getCondition() {
		return condition;
	}

	public void setCondition(QueryCondition condition) {
		this.condition = condition;
	}

	public Integer getStartNum() {
		return startNum;
	}

	public void setStartNum(Integer startNum) {
		this.startNum = startNum;
	}

	public Integer getEndNum() {
		return endNum;
	}

	public void setEndNum(Integer endNum) {
		this.endNum = endNum;
	}
}