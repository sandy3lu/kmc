CREATE TABLE `dict_constant`(
`id` int NOT NULL AUTO_INCREMENT COMMENT '自增id（常量编码）' ,
`value` varchar(64) not null COMMENT '常量名称',
`parent_id` int default null COMMENT '父id',
PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='通用字典表';