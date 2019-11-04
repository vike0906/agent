package com.vike.agent.common;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.io.Serializable;

/**
 * @author: lsl
 * @createDate: 2019/3/22
 */
@Setter
@Getter
public class PageLimit implements Serializable {

    private static final long serialVersionUID = -4541509938956089522L;

    /** 当前页 */
    private Integer pageNo;
    /** 页面条数 */
    private Integer limit;

    /** 分页默认页 */
    public static final Integer DEFAULT_PAGE_NO = 1;
    /** 分页默认的每页条数 */
    public static final Integer DEFAULT_LIMIT = 2;
    /** 默认倒序 */
    public static final Sort.Direction DEFAULT_SORT_DIRECTION = Sort.Direction.DESC;

    public PageLimit() {
        this.pageNo = DEFAULT_PAGE_NO;
        this.limit = DEFAULT_LIMIT;
    }

    public PageLimit(Integer pageNo, Integer limit) {
        this.pageNo = pageNo;
        this.limit = limit;
    }

    public PageRequest page(){
        return PageRequest.of(this.pageNo-1, this.limit);
    }

    public PageRequest page(Sort sort){
        return PageRequest.of(this.pageNo-1, this.limit, sort);
    }

    public PageRequest page(String property){
        return page(true, property);
    }

    public PageRequest page(boolean desc, String property){
        Sort.Direction direction = DEFAULT_SORT_DIRECTION;
        if(!desc){
            direction = Sort.Direction.ASC;
        }
        return PageRequest.of(this.pageNo-1, this.limit,
                direction, property);
    }


}
