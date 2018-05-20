alter table trans_goods add storage varchar(50);
comment on column trans_goods.storage is '可采储量';

alter table sys_attach_templet_dtl add file_desc varchar2(4000);
alter table sys_attach_templet_dtl add file_size int;
comment on column sys_attach_templet_dtl.file_desc is '附件描述，以提醒上传附件人员须上传内容';
comment on column sys_attach_templet_dtl.file_size is '限制文件大小，单位KB';

comment on column trans_bidder.bidder_type is '竞买人类别。0土地竞买人，1房产竞买人，2矿产竞买人，3耕指竞买人';