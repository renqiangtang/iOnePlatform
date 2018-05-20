create table WF_NODE 
(
   ID                   VARCHAR2(50)         not null,
   TEMPLATEID           VARCHAR2(50),
   NAME                 VARCHAR2(50),
   TYPE                 VARCHAR2(50),
   PARENTID             VARCHAR2(50),
   NEXTID               VARCHAR2(50),
   MEMO                 VARCHAR2(4000),
   constraint PK_WF_NODE primary key (ID)
)
tablespace EPF_SYSTEM;

comment on table WF_NODE is
'���̽ڵ��';

comment on column WF_NODE.TEMPLATEID is
'����ģ��ID';

comment on column WF_NODE.NAME is
'�ڵ�����';

comment on column WF_NODE.TYPE is
'�ڵ�����';

comment on column WF_NODE.PARENTID is
'��ID';

comment on column WF_NODE.NEXTID is
'��һ��ID';

comment on column WF_NODE.MEMO is
'��ע';

/*==============================================================*/
/* Table: WF_NODEUSER                                           */
/*==============================================================*/
create table WF_NODEUSER 
(
   ID                   VARCHAR2(50)         not null,
   NODEID               VARCHAR2(50),
   TYPE                 VARCHAR2(50),
   VALUE                VARCHAR2(4000),
   MEMO                 VARCHAR2(4000),
   constraint PK_WF_NODEUSER primary key (ID)
)
tablespace EPF_SYSTEM;

comment on table WF_NODEUSER is
'���̽ڵ�ִ���˱�';

comment on column WF_NODEUSER.NODEID is
'���̽ڵ�ID';

comment on column WF_NODEUSER.TYPE is
'����';

comment on column WF_NODEUSER.VALUE is
'ֵ';

comment on column WF_NODEUSER.MEMO is
'��ע';

/*==============================================================*/
/* Table: WF_PARAMETER                                          */
/*==============================================================*/
create table WF_PARAMETER 
(
   ID                   VARCHAR2(50)         not null,
   REFID                VARCHAR2(50),
   NAME                 VARCHAR2(4000),
   VALUE                VARCHAR2(4000),
   MEMO                 VARCHAR2(4000),
   constraint PK_WF_PARAMETER primary key (ID)
)
tablespace EPF_SYSTEM;

comment on table WF_PARAMETER is
'���̲�����';

comment on column WF_PARAMETER.REFID is
'����ID';

comment on column WF_PARAMETER.NAME is
'������';

comment on column WF_PARAMETER.VALUE is
'����ֵ';

comment on column WF_PARAMETER.MEMO is
'��ע';

/*==============================================================*/
/* Table: WF_PROCESS                                            */
/*==============================================================*/
create table WF_PROCESS 
(
   ID                   VARCHAR2(50)         not null,
   TEMPLATEID           VARCHAR2(50),
   CURNODEID            VARCHAR2(50),
   STATE                VARCHAR2(50),
   MEMO                 VARCHAR2(100),
   constraint PK_WF_PROCESS primary key (ID)
)
tablespace EPF_SYSTEM;

comment on table WF_PROCESS is
'����ʵ����';

comment on column WF_PROCESS.TEMPLATEID is
'����ģ��ID';

comment on column WF_PROCESS.CURNODEID is
'��ǰ�ڵ�ID';

comment on column WF_PROCESS.STATE is
'����״̬';

comment on column WF_PROCESS.MEMO is
'��ע';

/*==============================================================*/
/* Table: WF_TASKNODE                                           */
/*==============================================================*/
create table WF_TASKNODE 
(
   ID                   VARCHAR2(50)         not null,
   TEMPLATEID           VARCHAR2(50),
   CURRENTNODE          VARCHAR2(50),
   TAG1                 VARCHAR2(50),
   TAG2                 NUMBER(18,2),
   TIME                 VARCHAR2(50),
   USERID               VARCHAR2(50),
   MEMO                 VARCHAR2(4000),
   constraint PK_WF_TASKNODE primary key (ID)
)
tablespace EPF_SYSTEM;

comment on table WF_TASKNODE is
'�����';

comment on column WF_TASKNODE.TEMPLATEID is
'����ʵ��ID';

comment on column WF_TASKNODE.CURRENTNODE is
'��ǰ�ڵ�ID';

comment on column WF_TASKNODE.TAG1 is
'��ʶ1';

comment on column WF_TASKNODE.TAG2 is
'��ʶ2';

comment on column WF_TASKNODE.TIME is
'ִ��ʱ��';

comment on column WF_TASKNODE.USERID is
'ִ����';

comment on column WF_TASKNODE.MEMO is
'��ע';

/*==============================================================*/
/* Table: WF_TASKNODEUSER                                       */
/*==============================================================*/
create table WF_TASKNODEUSER 
(
   ID                   VARCHAR2(50)         not null,
   TASKNODEID           VARCHAR2(50),
   TYPE                 VARCHAR2(50),
   VALUE                VARCHAR2(4000),
   MEMO                 VARCHAR2(4000),
   constraint PK_WF_TASKNODEUSER primary key (ID)
)
tablespace EPF_SYSTEM;

comment on table WF_TASKNODEUSER is
'����ִ���˱�';

comment on column WF_TASKNODEUSER.TASKNODEID is
'����ڵ�ID';

comment on column WF_TASKNODEUSER.TYPE is
'����';

comment on column WF_TASKNODEUSER.VALUE is
'ֵ';

comment on column WF_TASKNODEUSER.MEMO is
'��ע';

/*==============================================================*/
/* Table: WF_TEMPLATE                                           */
/*==============================================================*/
create table WF_TEMPLATE 
(
   ID                   VARCHAR2(50)         not null,
   INSTANCECOUNT        VARCHAR2(50),
   MEMO                 VARCHAR2(4000),
   constraint PK_WF_TEMPLATE primary key (ID)
)
tablespace EPF_SYSTEM;

comment on table WF_TEMPLATE is
'����ģ���';

comment on column WF_TEMPLATE.INSTANCECOUNT is
'ʵ����';

comment on column WF_TEMPLATE.MEMO is
'��ע';

/*==============================================================*/
/* Table: WF_TIMEOUT                                            */
/*==============================================================*/
create table WF_TIMEOUT 
(
   ID                   VARCHAR2(50)         not null,
   PROCESSID            VARCHAR2(50),
   TYPE                 INTEGER,
   BEGINTIME            DATE,
   CLAZZ                VARCHAR2(200),
   STATE                INTEGER,
   MEMO                 VARCHAR2(4000),
   constraint PK_WF_TIMEOUT primary key (ID)
)
tablespace EPF_SYSTEM;

comment on table WF_TIMEOUT is
'��ʱ����';

comment on column WF_TIMEOUT.PROCESSID is
'����ʵ��ID';

comment on column WF_TIMEOUT.TYPE is
'����';

comment on column WF_TIMEOUT.BEGINTIME is
'��ʼʱ��';

comment on column WF_TIMEOUT.CLAZZ is
'������';

comment on column WF_TIMEOUT.STATE is
'״̬';

comment on column WF_TIMEOUT.MEMO is
'��ע';

