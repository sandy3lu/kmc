package com.yunjing.eurekaclient2.web.entity;

import com.yunjing.eurekaclient2.common.base.BaseEntity;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * key管理表
 * </p>
 *
 * @author scyking-auto
 * @since 2019-01-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class Keys extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 申请创建key的用户id
     */
    private Integer userId;

    /**
     * key的类型
     */
    private String keyType;

    /**
     * key的内容
     */
    private String content;

    /**
     * private key关联的public key
     */
    private Integer relatedKey;

    /**
     * key的签名值
     */
    private String signature;

    /**
     * 签名用的key
     */
    private Integer signKey;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
