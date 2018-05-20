--��ȡGUID
create or replace function get_guid(in_secure in integer := null) return varchar2 is
  guid varchar2(64);
begin
  guid := SYS_GUID();
  if in_secure is null or in_secure = 1 then
    return guid;
  else
    return
      '{' || substr(guid, 1, 8) || '-' || substr(guid, 9, 4)
      || '-' || substr(guid, 13, 4) || '-' || substr(guid, 17, 4)
      || '-' || substr(guid, 21, 12) || '}';
  end if;
end get_guid;
/

--MD5����
create or replace function ENCODE_MD5(input_string VARCHAR2) return varchar2
IS
  raw_input RAW(128) := UTL_RAW.CAST_TO_RAW(input_string);
  decrypted_raw RAW(2048);
  error_in_input_buffer_length EXCEPTION;
BEGIN
  if input_string is null then
    return null;
  else
    sys.dbms_obfuscation_toolkit.MD5(input => raw_input, checksum => decrypted_raw);
    return lower(rawtohex(decrypted_raw));
  end if;
END ENCODE_MD5;
/

--BASE64����
create or replace function ENCODE_BASE64(input_string VARCHAR2, input_convert_dir integer := 0) return varchar2
IS
  result varchar2(4000);
BEGIN
--�ַ���Base64���뼰������
if input_convert_dir is null or input_convert_dir = 0 then
--��ָ���ַ���ת��ΪBase64�ַ�������RAW�������ͣ�
  select utl_raw.cast_to_varchar2(utl_encode.base64_encode(utl_raw.cast_to_raw(input_string))) into result from dual;
else
--��ָ��Base64��ʽ�ַ���ת�����ַ�������RAW�������ͣ�
  select utl_raw.cast_to_varchar2(utl_encode.base64_decode(utl_raw.cast_to_raw(input_string))) into result from dual;
end if;
return result;
END ENCODE_BASE64;
/

--��ȡָ�����ַ����ָ����Ӵ���
create or replace function sub_str_count(in_parent varchar2, in_split varchar2,
  in_ignore_case number := 0) return integer is
  parent_str varchar2(4000);
  split_str varchar2(4000);
  split_index integer;
  result integer;
begin
  --��ȡ�������Ӵ�������
  if in_parent is null then
    result := -1;
    return result;
  end if;
  result := 0;
  if in_ignore_case = 1 then
    parent_str := upper(in_parent);
    split_str := upper(in_split);
  else
    parent_str := in_parent;
    split_str := in_split;
  end if;
  split_index := instr(parent_str, split_str);
  while split_index > 0 loop
    result := result + 1;
    split_index := instr(parent_str, split_str, split_index + length(split_str));
  end loop;
  return result;
end sub_str_count;
/

--��ȡָ���ַ����ָ�������λ�Ӵ�
create or replace function copy_sub_str(in_parent varchar2, in_index integer, in_split varchar2,
  in_ignore_case integer := 0) return varchar2 is
  compare_parent varchar2(4000);
  compare_split varchar2(4000);
  split_count integer;
  pos integer;
  pos1 integer;
  result varchar2(4000);
begin
  --��ȡָ��������ָ���ָ���������λ�õ��Ӵ�
  result := null;
  if (in_parent is null) or (in_index < 0) then
    return result;
  end if;
  if in_split is null then
    if in_index = 0 then
      result := in_parent;
    end if;
    return result;
  end if;
  if in_ignore_case = 1 then
    compare_parent := upper(in_parent || in_split);
    compare_split := upper(in_split);
  else
    compare_parent := in_parent || in_split;
    compare_split := in_split;
  end if;
  if instr(compare_parent, compare_split) = 0 then
    if in_index = 0 then
      result := in_parent;
    end if;
    return result;
  end if;
  split_count := sub_str_count(compare_parent, compare_split, in_ignore_case);
  if in_index > split_count then
    return null;
  end if;
  pos := instr(compare_parent, compare_split, 1, in_index + 1);
  if pos = 0 then
    return null;
  else
    if in_index = 0 then
      result := substr(in_parent, 1, pos - 1);
    else
      pos1 := instr(compare_parent, compare_split, 1, in_index);
      result := substr(in_parent, pos1 + length(compare_split), pos - pos1 - length(compare_split));
    end if;
    return result;
  end if;
end copy_sub_str;
/

--����ַ������Ƿ���ڲ���
create or replace function param_exists(in_params varchar2, in_param_name varchar2,
  in_ignore_case integer := 0, in_split varchar2 := ';', in_equal varchar2 := '=') return integer is
  params_str varchar2(4000);
  param_name varchar2(4000);
  split_str varchar2(4000);
  equal_str varchar2(4000);
  result number(1);
begin
  --in_params: Դ�ַ���,����"a=111;b=222;c;d=333"
  --in_ignore_case: ���������Դ�Сд
  --��"abc=124;efg;xyz=333"�м��"abc"����2����ʾ��ֵ����efg�򷵻�1��ʾ��ֵ����������xxx�򷵻�0
  --����ֵ��0�޲�����1�в�����ֵ��2�в�����ֵ
  if in_params is null then
    return 0;
  end if;
  if in_ignore_case = 1 then
    params_str := upper(in_split || in_params || in_split);
    param_name := upper(in_param_name);
    split_str := upper(in_split);
    equal_str := upper(in_equal);
  else
    params_str := in_split || in_params || in_split;
    param_name := in_param_name;
    split_str := in_split;
    equal_str := in_equal;
  end if;
  if instr(params_str, split_str || param_name || equal_str) > 0 then
    result := 2;
  elsif instr(params_str, split_str || param_name || split_str) > 0 then
    result := 1;
  else
    result := 0;
  end if;
  return result;
end param_exists;
/

--��ȡ�ַ����в���ֵ
create or replace function get_param_value(in_params varchar2, in_param_name varchar2,
  in_ignore_case integer := 0, in_split varchar2 := ';', in_equal varchar2 := '=') return varchar2 is
  params_str varchar2(4000);
  param_name varchar2(4000);
  split_str varchar2(4000);
  equal_str varchar2(4000);
  result varchar2(4000);
  pos integer;
begin
  --�Ӳ���������"abc=123;xyz=666;mnq=888"����ȡָ����������ֵ����in_param_name="xyz"����"666"
  if in_params is null or param_exists(in_params, in_param_name, in_ignore_case, in_split, in_equal) = 0 then
    return null;
  end if;
  if in_ignore_case = 1 then
    params_str := upper(in_split || in_params || in_split);
    param_name := upper(in_param_name);
    split_str := upper(in_split);
    equal_str := upper(in_equal);
  else
    params_str := in_split || in_params || in_split;
    param_name := in_param_name;
    split_str := in_split;
    equal_str := in_equal;
  end if;
  if instr(params_str, split_str || param_name || equal_str) > 0 then
    result := substr(in_split || in_params, instr(params_str, split_str || param_name || equal_str) + length(split_str || param_name || equal_str), length(params_str));
    if instr(upper(result), upper(split_str)) > 0 then
      pos := instr(upper(result), upper(split_str));
      result := substr(result, 1, pos - 1);
      --result := substr(result, 1, instr(upper(result), upper(split_str)) - 1);
    end if;
  else
    result := null;
  end if;
  return result;
end get_param_value;
/

--д�ַ������Ӵ�ֵ
create or replace function set_param_value(in_params varchar2, in_param_name varchar2, in_param_value varchar2,
  in_ignore_case integer := 0, in_split varchar2 := ';', in_equal varchar2 := '=', not_exists_add integer := 1) return varchar2 is
  params_str varchar2(4000);
  param_name varchar2(4000);
  split_str varchar2(4000);
  equal_str varchar2(4000);
  left_str varchar2(4000);
  right_str varchar2(4000);
  pos integer;
begin
  --�Ӳ���������"abc=123;xyz=666;mnq=888"������ָ����������ֵ
  if in_param_name is null then
    return in_params;
  end if;
  if in_params is null then
    if not_exists_add = 1 then
      return in_param_name || in_equal || in_param_value;
    else
      return null;
    end if;
  end if;
  if param_exists(in_params, in_param_name, in_ignore_case, in_split, in_equal) = 0 then
    if not_exists_add = 1 then
      return in_params || in_split || in_param_name || in_equal || in_param_value;
    else
      return in_params;
    end if;    
  end if;
  if in_ignore_case = 1 then
    params_str := upper(in_split || in_params || in_split);
    param_name := upper(in_param_name);
    split_str := upper(in_split);
    equal_str := upper(in_equal);
  else
    params_str := in_split || in_params || in_split;
    param_name := in_param_name;
    split_str := in_split;
    equal_str := in_equal;
  end if;
  if instr(params_str, split_str || param_name || equal_str) > 0 then
    left_str := substr(in_split || in_params, 1, instr(params_str, split_str || param_name || equal_str) - 1);
    left_str := substr(left_str, length(in_split) + 1, length(left_str));
    right_str := substr(in_split || in_params, instr(params_str, split_str || param_name || equal_str) + length(split_str || param_name || equal_str), length(params_str));
    if instr(upper(right_str), upper(split_str)) > 0 then
      pos := instr(upper(right_str), upper(split_str));
      right_str := substr(right_str, pos + length(split_str), length(right_str));
    else
      right_str := null;
    end if;
  else
    left_str := substr(in_split || in_params, 1, instr(params_str, split_str || param_name || in_split) - 1);
    left_str := substr(left_str, length(in_split) + 1, length(left_str));
    right_str := substr(in_split || in_params, instr(params_str, split_str || param_name || in_split) + length(split_str || param_name || in_split), length(params_str));
  end if;
  if left_str is not null then
    left_str := left_str || in_split;
  end if;
  if right_str is not null then
    right_str := in_split || right_str;
  end if;
  return left_str || in_param_name || in_equal || in_param_value || right_str;
end set_param_value;
/

--ǿ��ɾ�������������������ʱ��
CREATE GLOBAL TEMPORARY TABLE DELETE_FOREIGN_DATA_TAB(
    ID INTEGER,
    TABLE_NAME VARCHAR2(50),
    COLUMN_NAME VARCHAR2(50),
    CHILD_TABLE_NAME VARCHAR2(50),
    CHILD_COLUMN_NAME VARCHAR2(50),
    DEL_SQL VARCHAR2(4000),
    SELECT_SQL VARCHAR2(4000),
    FLAG NUMBER(1) DEFAULT(0)
    ) ON COMMIT delete ROWS;

--ǿ��ɾ����������������ݹ���
create or replace procedure delete_foreign_data(in_table_name varchar2, in_key_field varchar2,
  in_key_value varchar2, in_key_value_type integer := 0, in_delete_level integer := 0) is
  delete_level integer;
  key_value_type integer;
  exec_sql varchar2(4000);
  row_count integer;
  record_count integer;
  child_primary_field varchar2(50);
  operate_tabs varchar2(4000);
  USER_DEFINE_EXCEIPTON EXCEPTION; 
  PRAGMA EXCEPTION_INIT(USER_DEFINE_EXCEIPTON, -20001); 
begin
/*
in_table_name����Ҫɾ�����ݵı���
in_key_field����Ҫɾ�����ݵı������ֶ���
in_key_value����Ҫɾ�����ݵı������ֶ�ֵ
in_key_value_type��in_key_valueֵ�����͡�0in_key_field�ֶ�ֵ��֧�ֵ������߶����1��ѯ��䣬���ؽ����ֻ��һ��in_key_field�ֶΣ���������ֶΣ�
in_delete_level��ɾ������0ֻɾ����¼����in_key_field���������ֶ�ʱҲ���˷�ʽ����1���������ֶ�ֵΪNULL(���ֶβ�����Ϊnullʱ��ʧ��)��2ɾ���������ü�¼(ǿ��ɾ��ʹ�ô�ֵ)
****�ݲ�֧��****
1���������ֶεı�
2��Ƕ�����õ����ñ���trans_target��trans_goods��trans_target_goods_rel���������ɾ����������ڲ��������޷�ɾ����ɶ���Ϊ��������
****ע��****
1���˹��̽���ֻ��Ϊɾ��������ʱʹ�ã�һ�������ʲô���ݶ�����ɾ�������������û�����
2����ʹ��ʹ�ô˹���ɾ������Ҳ���ܱ��������ɾ��ʧ�ܣ���û�а��߼���ɾ��������޷����delete_target_data��delete_workflow_data֮����̵Ĺ��ܣ�
3���贴��������ʱ��
CREATE GLOBAL TEMPORARY TABLE DELETE_FOREIGN_DATA_TAB(
    ID INTEGER,
    TABLE_NAME VARCHAR2(50),
    COLUMN_NAME VARCHAR2(50),
    CHILD_TABLE_NAME VARCHAR2(50),
    CHILD_COLUMN_NAME VARCHAR2(50),
    DEL_SQL VARCHAR2(4000),
    SELECT_SQL VARCHAR2(4000),
    FLAG NUMBER(1) DEFAULT(0)
    ) ON COMMIT delete ROWS;
****����ʾ��****
begin
  delete_foreign_data('trans_target', 'id', '025012340000002179;025012340000002180', 0, 2);
  delete_foreign_data('trans_target', 'id', 'select id from trans_target where id = ''025012340000002178''', 1, 2);
end;
*/
  if in_table_name is null or in_key_field is null or in_key_value is null then
    return;
  end if;
  select decode(in_delete_level, 1, 1, 2, 2, 0), decode(in_key_value_type, 1, 1, 0) into delete_level, key_value_type from dual;
  select count(*) into row_count from user_tab_columns where upper(table_name) = upper(in_table_name)
    and upper(column_name) = upper(in_key_field);
  if row_count = 0 then
    RAISE_APPLICATION_ERROR(-20001, 'ָ����' || in_table_name || '�����ֶ�' || in_key_field || '��');
  end if;
  select count(*) into row_count from user_constraints c, user_cons_columns col
    where c.owner = user and c.owner = col.owner
      and c.constraint_name = col.constraint_name and c.constraint_type = 'P'
      and upper(c.table_name) = upper(in_table_name) and upper(col.column_name) = upper(in_key_field);
  record_count := 0;
  if key_value_type = 0 then--ֱ�Ӵ�ֵ
    insert into DELETE_FOREIGN_DATA_TAB(ID, TABLE_NAME, COLUMN_NAME, CHILD_TABLE_NAME, CHILD_COLUMN_NAME, DEL_SQL, SELECT_SQL, FLAG)
      values(record_count, null, null, in_table_name, in_key_field,
      'delete from ' || in_table_name || ' where instr('';' || in_key_value || ';'', '';'' || ' || in_key_field || ' || '';'') > 0;',
      'select '  || in_key_field ||  ' from ' || in_table_name || ' where instr('';' || in_key_value || ';'', '';'' || ' || in_key_field || ' || '';'') > 0',
      0);
  else--�����Ӳ�ѯ
    insert into DELETE_FOREIGN_DATA_TAB(ID, TABLE_NAME, COLUMN_NAME, CHILD_TABLE_NAME, CHILD_COLUMN_NAME, DEL_SQL, SELECT_SQL, FLAG)
      values(record_count, null, null, in_table_name, in_key_field,
      'delete from ' || in_table_name || ' where ' || in_key_field || ' in (' || in_key_value || ');',
      in_key_value,
      0);
  end if;
  record_count := record_count + 1;
  if row_count > 0 then
    if delete_level = 1 then--�������������ֶ�ֵΪNULL
      for rec_constraint in (select c.CONSTRAINT_NAME, c.TABLE_NAME, col.column_name from USER_CONSTRAINTS c, user_cons_columns col
        where c.owner = user and c.owner = col.owner and c.constraint_name = col.constraint_name
          and c.CONSTRAINT_TYPE = 'R' and c.R_CONSTRAINT_NAME in (select CONSTRAINT_NAME from USER_CONSTRAINTS where CONSTRAINT_TYPE = 'P' and TABLE_NAME = upper(in_table_name))) loop
        if key_value_type = 0 then
          insert into DELETE_FOREIGN_DATA_TAB(ID, TABLE_NAME, COLUMN_NAME, CHILD_TABLE_NAME, CHILD_COLUMN_NAME, DEL_SQL, SELECT_SQL, FLAG)
            values(record_count, in_table_name, in_key_field, rec_constraint.table_name, rec_constraint.column_name,
            'update ' || rec_constraint.table_name || ' set ' || rec_constraint.column_name || ' = null where instr('';' || in_key_value || ';'', '';'' || ' || rec_constraint.column_name || ' || '';'') > 0;',
            null, 1);
        else
          insert into DELETE_FOREIGN_DATA_TAB(ID, TABLE_NAME, COLUMN_NAME, CHILD_TABLE_NAME, CHILD_COLUMN_NAME, DEL_SQL, SELECT_SQL, FLAG)
            values(record_count, in_table_name, in_key_field, rec_constraint.table_name, rec_constraint.column_name,
            'update ' || rec_constraint.table_name || ' set ' || rec_constraint.column_name || ' = null where ' || rec_constraint.column_name || ' in (' || in_key_value || ');',
            null, 1);
        end if;
        record_count := record_count + 1;
      end loop;
    elsif delete_level = 2 then--ɾ�����������ֶ�
      operate_tabs := null;
      <<RELOOP>>
      for rec_foreign in (select * from DELETE_FOREIGN_DATA_TAB where FLAG = 0 order by ID) loop
        update DELETE_FOREIGN_DATA_TAB set FLAG = 1 where id = rec_foreign.ID;
        if operate_tabs is not null and rec_foreign.child_table_name is not null and param_exists(operate_tabs, rec_foreign.child_table_name) >= 1 then
          exit;
        end if;
        select count(*) into row_count from USER_CONSTRAINTS c, user_cons_columns col
          where c.owner = user and c.owner = col.owner and c.constraint_name = col.constraint_name
            and c.CONSTRAINT_TYPE = 'R' and c.R_CONSTRAINT_NAME in (select CONSTRAINT_NAME from USER_CONSTRAINTS where CONSTRAINT_TYPE = 'P' and TABLE_NAME = upper(rec_foreign.child_table_name));
        if row_count = 0 then
          exit;
        end if;
        if operate_tabs is null then
          operate_tabs := rec_foreign.child_table_name;
        else
          operate_tabs := operate_tabs || ';' || rec_foreign.child_table_name;
        end if;
        for rec_constraint in (select c.CONSTRAINT_NAME, c.TABLE_NAME, col.column_name from USER_CONSTRAINTS c, user_cons_columns col
          where c.owner = user and c.owner = col.owner and c.constraint_name = col.constraint_name
            and c.CONSTRAINT_TYPE = 'R' and c.R_CONSTRAINT_NAME in (select CONSTRAINT_NAME from USER_CONSTRAINTS where CONSTRAINT_TYPE = 'P' and TABLE_NAME = upper(rec_foreign.child_table_name))) loop
          select count(*) into row_count from user_constraints c, user_cons_columns col
            where c.owner = user and c.owner = col.owner
              and c.constraint_name = col.constraint_name and c.constraint_type = 'P'
              and upper(c.table_name) = upper(rec_constraint.table_name);
          if row_count = 1 then--���������ֶ�����Ҫ�ټ���Ƿ������ñ�
            select col.column_name into child_primary_field from user_constraints c, user_cons_columns col
              where c.owner = user and c.owner = col.owner
                and c.constraint_name = col.constraint_name and c.constraint_type = 'P'
                and upper(c.table_name) = upper(rec_constraint.table_name);
            insert into DELETE_FOREIGN_DATA_TAB(ID, TABLE_NAME, COLUMN_NAME, CHILD_TABLE_NAME, CHILD_COLUMN_NAME, DEL_SQL, SELECT_SQL, FLAG)
              values(record_count, rec_foreign.child_table_name, rec_foreign.child_column_name, rec_constraint.table_name, rec_constraint.column_name,
              'delete from ' || rec_constraint.table_name || ' where ' || rec_constraint.column_name || ' in (' || rec_foreign.select_sql || ');',
              'select ' || child_primary_field || ' from ' || rec_constraint.table_name || ' where ' || rec_constraint.column_name || ' in (' || rec_foreign.select_sql || ')',
              0);
            record_count := record_count + 1;
          else--�ӱ����������������ֶ�����Ҫ�ټ���Ƿ������ñ�
            insert into DELETE_FOREIGN_DATA_TAB(ID, TABLE_NAME, COLUMN_NAME, CHILD_TABLE_NAME, CHILD_COLUMN_NAME, DEL_SQL, SELECT_SQL, FLAG)
              values(record_count, rec_foreign.child_table_name, rec_foreign.child_column_name, rec_constraint.table_name, rec_constraint.column_name,
              'delete from ' || rec_constraint.table_name || ' where ' || rec_constraint.column_name || ' in (' || rec_foreign.select_sql || ');',
              null, 1);
              record_count := record_count + 1;
          end if;
        end loop;
      end loop;
      select count(*) into row_count from DELETE_FOREIGN_DATA_TAB where FLAG = 0;
      if row_count > 0 then
        goto RELOOP;
      end if;
    end if;
  end if;
  --ִ�����ɵ�ɾ�����
  record_count := 0;
  for rec_foreign in (select * from DELETE_FOREIGN_DATA_TAB order by ID desc) loop
    if exec_sql is null then
      exec_sql := 'begin';
    end if;
    exec_sql := exec_sql || ' ' || rec_foreign.del_sql;
    record_count := record_count + 1;
    --����10�����ñ���ɾ��һ�Σ�����SQL����������exec_sql��������
    if record_count >= 10 or length(exec_sql) > 3500 then
      exec_sql := exec_sql || ' ' || 'end;';
      execute immediate exec_sql;
      exec_sql := null;
      record_count := 0;
    end if;    
  end loop;
  if exec_sql is not null then
    exec_sql := exec_sql || ' ' || 'end;';
    execute immediate exec_sql;
  end if;
/*exception
  when USER_DEFINE_EXCEIPTON then
    RAISE USER_DEFINE_EXCEIPTON;
  when others then
    rollback;
    RAISE;*/
end delete_foreign_data;
/

create or replace function get_role_rel(in_ref_id in sys_role_rel.id%type,
  in_ref_type in sys_role_rel.ref_type%type,
  in_return_field varchar2 := 'name',
  in_split in varchar2 := ',')
return varchar2
as
  result Varchar2(8000) := '';
  cur_id sys_role.id%type;
  cur_no sys_role.name%type;
  cur_name sys_role.name%type;
  CURSOR ROLE_REL_CURSOR IS
    select id, no, name from sys_role
      where exists(select * from sys_role_rel where ref_type = in_ref_type and ref_id = in_ref_id and role_id = sys_role.id);
begin
  --��ȡ�����Ľ�ɫ��Ϣ
  OPEN ROLE_REL_CURSOR;
  LOOP
    FETCH ROLE_REL_CURSOR INTO cur_id, cur_no, cur_name;
    EXIT WHEN ROLE_REL_CURSOR%NOTFOUND;
    if lower(in_return_field) = 'id' then
      result := result || cur_id;
    elsif lower(in_return_field) = 'no' then
      result := result || cur_no;
    elsif lower(in_return_field) = 'name' then
      result := result || cur_name;
    end if;
    result := result || in_split;
  END LOOP;
  CLOSE ROLE_REL_CURSOR;
  if result is not null then
    result := substr(result, 1, length(result) - length(in_split));
  end if;
  return result;
end get_role_rel;
/

create or replace function get_emp_job(in_emp_id in sys_emp.id%type,
  in_return_field varchar2 := 'name',
  in_split in varchar2 := ',')
return varchar2
as
  result varchar2(8000) := '';
  cur_id sys_job.id%type;
  cur_no sys_job.no%type;
  cur_name sys_job.name%type;
  cur_department_name sys_department.name%type;
  cur_organ_name sys_organ.name%type;
  CURSOR JOB_CURSOR IS
    select j.id, j.no, j.name, d.name as department_id, o.name as organ_name
      from sys_job j
      left join sys_department d on d.id = j.department_id
      left join sys_organ o on o.id = d.organ_id
      where exists(select * from sys_job_emp_rel where emp_id = in_emp_id and job_id = j.id);
begin
  --��ȡ��Ա�����ĸ�λ��Ϣ
  OPEN JOB_CURSOR;
  LOOP
    FETCH JOB_CURSOR INTO cur_id, cur_no, cur_name, cur_department_name, cur_organ_name;
    EXIT WHEN JOB_CURSOR%NOTFOUND;
    if lower(in_return_field) = 'id' then
      result := result || cur_id;
    elsif lower(in_return_field) = 'no' then
      result := result || cur_no;
    elsif lower(in_return_field) = 'name' then
      result := result || cur_name;
    elsif lower(in_return_field) = 'fullname' then
      result := result || cur_department_name || '.' || cur_name;
    elsif lower(in_return_field) = 'fullname1' then
      result := result || cur_organ_name || '.' || cur_department_name || '.' || cur_name;
    end if;
    result := result || in_split;
  END LOOP;
  CLOSE JOB_CURSOR;
  if result is not null then
    result := substr(result, 1, length(result) - length(in_split));
  end if;
  return result;
end get_emp_job;
/

create or replace function get_emp_department(in_emp_id in sys_emp.id%type,
  in_return_field varchar2 := 'name',
  in_split in varchar2 := ',')
return varchar2
as
  result Varchar2(8000) := '';
  cur_id sys_department.id%type;
  cur_no sys_department.no%type;
  cur_name sys_department.name%type;
  cur_organ_name sys_organ.name%type;
  CURSOR DEPARTMENT_CURSOR IS
    select d.id, d.no, d.name, o.name as organ_name
      from sys_department d
      left join sys_organ o on o.id = d.organ_id
      where exists(select * from sys_department_emp_rel where emp_id = in_emp_id and department_id = d.id);
begin
  --��ȡ��Ա�����Ĳ�����Ϣ
  OPEN DEPARTMENT_CURSOR;
  LOOP
    FETCH DEPARTMENT_CURSOR INTO cur_id, cur_no, cur_name, cur_organ_name;
    EXIT WHEN DEPARTMENT_CURSOR%NOTFOUND;
    if lower(in_return_field) = 'id' then
      result := result || cur_id;
    elsif lower(in_return_field) = 'no' then
      result := result || cur_no;
    elsif lower(in_return_field) = 'name' then
      result := result || cur_name;
    elsif lower(in_return_field) = 'fullname' then
      result := result || cur_organ_name || '.' || cur_name;
    end if;
    result := result || in_split;
  END LOOP;
  CLOSE DEPARTMENT_CURSOR;
  if result is not null then
    result := substr(result, 1, length(result) - length(in_split));
  end if;
  return result;
end get_emp_department;
/

create or replace function get_target_goods(in_target_id varchar2, in_return_field varchar2 := null, in_replace_value varchar2 := null) return varchar2 is
  result varchar2(4000);
  goods_cursor SYS_REFCURSOR;
  execute_sql varchar2(4000);
  main_goods_id trans_goods.id%type;
  return_field varchar2(4000);
  replace_field varchar2(4000);
  return_value varchar2(4000);
  replace_value varchar2(4000);
  row_count integer;
  is_return_ex_value integer;
  return_ex_field_name varchar(100);
begin
--��ȡ��ĵ��ڵ���Ϣ
--in_target_id: ���ID
--in_return_field: �����ֶ�����������trans_goods�������Ӧ����չ���д��ڵ��ֶ�
--in_replace_value: ָ���ֶ�in_return_field��Ҫ�滻��ֵ����ʽ��-1=ί����δ��֪;0=��;1=��;����ע��ֻ�����һ���޵Ⱥţ���Ϊ����ֵ���滻ֵ
  return_field := in_return_field;
  if return_field is null then
    return_field := 'no';
  end if;
  --�����trans_goods���ֶλ�����չ���е��ֶ�(sys_extend.field_no)
  is_return_ex_value := 0;
  select count(*) into row_count from user_tab_columns where lower(table_name) = 'trans_goods' and lower(column_name) = lower(return_field);
  if row_count = 0 then
    return_ex_field_name := return_field;
    return_field := 'field_value';
    is_return_ex_value := 1;
  end if;
  if in_replace_value is not null then
    replace_field := 'decode(' || return_field || ',';
    for i in 0..sub_str_count(in_replace_value, ';') loop
      replace_value := copy_sub_str(in_replace_value, i, ';');
      if instr(replace_value, '=') = 0 then
        replace_field := replace_field || '''' || replace_value || ''',';
        exit;
      else
        replace_field := replace_field || '''' || substr(replace_value, 1, instr(replace_value, '=') - 1) || ''', '''
          || substr(replace_value, instr(replace_value, '=') + 1, length(replace_value)) || ''',';
      end if;
    end loop;
    return_field := substr(replace_field, 1, length(replace_field) - 1) || ')';
  end if;
  select count(*) into row_count from trans_target_goods_rel where target_id = in_target_id and is_main_goods = 1;
  if row_count >= 1 then
    if is_return_ex_value = 1 then
      execute_sql := 'select id, ' || return_field || ' from (select g.*, ex.field_value, nvl(e.name, u.user_name) as emp_name, t.business_type'
        || chr(13) || '  from trans_goods g'
        || chr(13) || '  left join sys_extend ex on lower(ex.ref_table_name) = ''trans_goods'' and ex.ref_id = g.id and ex.field_no = ''' || return_ex_field_name || ''''
        || chr(13) || '  left join sys_user u on u.id = g.create_user_id'
        || chr(13) || '  left join sys_emp e on e.id = u.ref_id and u.user_type = 0'
        || chr(13) || '  left join trans_trust t on g.trust_id = t.id)'
        || chr(13) || 'where id in (select goods_id from trans_target_goods_rel where target_id = :in_target_id and is_main_goods = 1 and rownum <= 1)';
    else
      execute_sql := 'select id, ' || return_field || ' from (select g.*, nvl(e.name, u.user_name) as emp_name, t.business_type'
        || chr(13) || '  from trans_goods g'
        || chr(13) || '  left join sys_user u on u.id = g.create_user_id'
        || chr(13) || '  left join sys_emp e on e.id = u.ref_id and u.user_type = 0'
        || chr(13) || '  left join trans_trust t on g.trust_id = t.id)'
        || chr(13) || 'where id in (select goods_id from trans_target_goods_rel where target_id = :in_target_id and is_main_goods = 1 and rownum <= 1)';
    end if;
    execute immediate execute_sql into main_goods_id, result using in_target_id;
  end if;
  if is_return_ex_value = 1 then
    execute_sql := 'select ' || return_field || ' from (select g.*, ex.field_value, nvl(e.name, u.user_name) as emp_name, t.business_type'
        || chr(13) || '  from trans_goods g'
        || chr(13) || '  left join sys_extend ex on lower(ex.ref_table_name) = ''trans_goods'' and ex.ref_id = g.id and ex.field_no = ''' || return_ex_field_name || ''''
        || chr(13) || '  left join sys_user u on u.id = g.create_user_id'
        || chr(13) || '  left join sys_emp e on e.id = u.ref_id and u.user_type = 0'
        || chr(13) || '  left join trans_trust t on g.trust_id = t.id)'
        || chr(13) || 'where id in (select goods_id from trans_target_goods_rel where target_id = ''' || in_target_id || ''' and (is_main_goods is null or is_main_goods <> 1 or goods_id <> ''' || main_goods_id || '''))';
  else
    execute_sql := 'select ' || return_field || ' from (select g.*, nvl(e.name, u.user_name) as emp_name, t.business_type'
        || chr(13) || '  from trans_goods g'
        || chr(13) || '  left join sys_user u on u.id = g.create_user_id'
        || chr(13) || '  left join sys_emp e on e.id = u.ref_id and u.user_type = 0'
        || chr(13) || '  left join trans_trust t on g.trust_id = t.id)'
        || chr(13) || 'where id in (select goods_id from trans_target_goods_rel where target_id = ''' || in_target_id || ''' and (is_main_goods is null or is_main_goods <> 1 or goods_id <> ''' || main_goods_id || '''))';
  end if;
  open goods_cursor for execute_sql;
  loop
    fetch goods_cursor into return_value;
    exit when goods_cursor%notfound;
    if result is null then
      result := return_value;
    else
      result := result || ',' || return_value;
    end if;
  end loop;
  close goods_cursor;
  return result;
end get_target_goods;
/

create or replace trigger sys_role_ud
  before update or delete
  on sys_role
  for each row
begin
  if :old.role_type = 1 then
    if deleting then
      RAISE_APPLICATION_ERROR(-20001, '�ڲ���ɫ���ܱ�ɾ����');
    elsif updating and ((updating('no') and :old.no <> :new.no) or (updating('name') and :old.name <> :new.name)) then
      RAISE_APPLICATION_ERROR(-20001, '�ڲ���ɫ������Ϣ���ܱ��޸ġ�');
    end if;
  end if;
end sys_role_ud;
/

create or replace function get_random_no(in_no_len integer) return varchar2 is
  result varchar2(200);
  no_len integer;
  random_pos integer;
  random_num integer;
  row_count integer;
begin
--��ȡ�����
  if (in_no_len is null or in_no_len <= 0) then
    no_len := 8;
  else
    no_len := in_no_len;
  end if;
  select dbms_random.value(dbms_random.value(1, 100), dbms_random.value(5000, 10000)) into result from dual;
  result := replace(result, '.', '');
  if length(result) > no_len then
    result := substr(result, 1, no_len);
  else
    for i in 1..no_len loop
      random_pos := round(dbms_random.value(1, no_len));
      result := substr(result, 1, random_pos) + round(dbms_random.value(1, 9)) + substr(result, random_pos + 1, no_len);
    end loop;
  end if;
  return result;
end get_random_no;
/

--��������������
create sequence TRANS_LICENSE_APPLY_NO_SEQ
  minvalue 1
  maxvalue 999999999999
  start with 1
  increment by 1
  nocache;
/
--��������ɽ�ȷ����������
create sequence TRANS_LICENSE_TRANS_NO_SEQ
  minvalue 1
  maxvalue 999999999999
  start with 1
  increment by 1
  nocache;
/

create or replace function create_table_id(in_table_name in varchar2, in_key_field_name in varchar2 := null,
    in_return_type int := null, in_return_count int := null) return varchar2
    is PRAGMA AUTONOMOUS_TRANSACTION;
  cur_table_id sys_table.id%type;
  cur_table_no varchar2(4);
  cur_column_name varchar2(255);
  cur_canton_prefix varchar2(50);--����
  cur_prefix varchar2(50);--�Զ���ǰ׺
  cur_id_len int;
  is_add_cur_canton_prefix int;
  is_add_no_prefix int;
  new_table_no int;
  return_count int;
  id_prefix varchar2(50);--ID��ǰ׺
  cur_max_id integer;
  str_max_id varchar2(100);
  exec_sql varchar2(4000);
  row_count integer;
begin
--���ر����IDֵ(����)
--in_table_name: ����
--in_key_field_name: ������IDֵ�ֶΣ���һ�������������ֶΡ����Ϊnull���ߴ����ֶ�����Ч��ȡ�����ֶΣ�������������ֶ��򷵻�
--in_return_type: 1�������ID��ֵ(���÷�ת��Ϊ����)��2����ǰ׺������ֵ�������ID��
--in_return_count: ����Idֵ��������δ��ȱʡΪ1��ID�š�ʵ�������۴���ʲôֵ���صĶ��ǵ�һ�����õ�IDֵ��������in_return_count��ID���ѱ�ռ��
--����-1�������Ϊ�޷�ȡ����ȷ�����IDֵ
  if in_return_count is null or in_return_count <= 0 then
    return_count := 1;
  else
    return_count := in_return_count;
  end if;
  --δ������
  if in_table_name is null then
    dbms_output.put_line('δ������Ҫ����ID�ŵı�����');
    return(-1);
  end if;
  --�������Ƿ�Ϊ��Ч����
  select count(*) into row_count from user_tables
    where upper(table_name) = upper(in_table_name);
  if row_count = 0 then
    dbms_output.put_line('������Ҫ����ID�ŵı��������ڡ�');
    return(-2);
  end if;
  --����ֵ�ֶ�
  if in_key_field_name is not null then
    cur_column_name := in_key_field_name;
  end if;
  select count(*) into row_count from user_tab_columns where table_name = upper(in_table_name) and upper(column_name) = upper(cur_column_name);
  --���ؼ��ֶ�������Ч��ȡ��������Ϣ
  if cur_column_name is null or row_count = 0 then
    select count(*) into row_count from user_constraints c, user_cons_columns col
      where c.owner = user and c.owner = col.owner
        and c.constraint_name = col.constraint_name and c.constraint_type = 'P'
        and upper(c.table_name) = upper(in_table_name);
    if row_count <= 0 or row_count > 1 then--ʵ�ʱ�����������������
      dbms_output.put_line('δ������Ҫ����ID�ŵı��еļ�ֵ�ֶΣ�ϵͳҲ�޷��ӱ�ṹԪ�����л�ȡ�����Ϣ��');
      return(-3);
    else
      select column_name into cur_column_name from user_constraints c, user_cons_columns col
        where c.owner = user and c.owner = col.owner
          and c.constraint_name = col.constraint_name and c.constraint_type = 'P'
          and upper(c.table_name) = upper(in_table_name);
    end if;
  end if;
  --ȡ��ע����Ϣ
  cur_prefix := null;
  select count(*) into row_count from sys_table where upper(name) = upper(in_table_name)
    and (cur_column_name is null or upper(key_field_name) = upper(cur_column_name));
  if row_count > 0 then
    select id, lpad(no, 4, '0'), decode(cur_canton_prefix, 1, 1, 0), decode(no_prefix, 1, 1, 0), prefix, id_len, nvl(max_id, 0)
        into cur_table_id, cur_table_no, is_add_cur_canton_prefix, is_add_no_prefix, cur_prefix, cur_id_len, cur_max_id
      from sys_table
      where upper(name) = upper(in_table_name)
        and (cur_column_name is null or upper(key_field_name) = upper(cur_column_name));
  else
    is_add_cur_canton_prefix := 0;
    select count(*) into row_count from sys_table where upper(name) = upper(in_table_name);
    if row_count > 0 then--ͬһ�����ֶ�ʱʹ��ͬһ���
      select lpad(no, 4, '0') into cur_table_no from sys_table where upper(name) = upper(in_table_name) and rownum <= 1;
    else
      new_table_no := 0;
      loop
        new_table_no := new_table_no + 1;
        if new_table_no >= 9999 then
          dbms_output.put_line('sys_table��ע��ı����Ѿ����������������9999��������ɾ��һЩ���õı�ע���¼����9999Ϊ��š�');
          exit;
        end if;
        cur_table_no := lpad(new_table_no, 4, '0');
        select count(*) into row_count from sys_table where no = cur_table_no;
        if row_count = 0 then
          exit;
        end if;
      end loop;
    end if;
    cur_table_id := get_guid;
    is_add_no_prefix := 1;
    insert into sys_table(id, no, name, key_field_name, max_id)
      values(cur_table_id, cur_table_no, in_table_name, cur_column_name, 0);
    commit;
  end if;
  if cur_table_no is null then
    cur_table_no := '0000';
  end if;
  select id into cur_table_id from sys_table where id = cur_table_id for update;--�������
  --ת���Զ���ǰ׺
  if (cur_prefix is not null) then
    cur_prefix := replace(cur_prefix, '[YYYY]', to_char(sysdate, 'yyyy'));
    cur_prefix := replace(cur_prefix, '[YY]', to_char(sysdate, 'yy'));
    cur_prefix := replace(cur_prefix, '[MM]', to_char(sysdate, 'mm'));
    cur_prefix := replace(cur_prefix, '[DD]', to_char(sysdate, 'dd'));
  end if;
  --ȡ����
  if is_add_cur_canton_prefix = 1 then
    select count(*) into row_count from sys_params where no = 'system_default_organ' and rownum <= 1;
    if row_count > 0 then
      select lpad(no, 6, '0') into cur_canton_prefix from sys_canton where id in (select id from sys_params where no = 'system_default_organ') and rownum <= 1;
    end if;
  end if;
  if cur_canton_prefix is null then
    cur_canton_prefix := '000000';
  end if;
  id_prefix := null;
  if is_add_cur_canton_prefix = 1 then
    id_prefix := id_prefix || cur_canton_prefix;
  end if;
  if is_add_no_prefix = 1 then
    id_prefix := id_prefix || cur_table_no;
  end if;
  id_prefix := id_prefix || cur_prefix;
  if in_return_type = 2 then
    rollback;--�����½���
    return id_prefix;
  end if;
  if cur_id_len is null or cur_id_len <= 0 or cur_id_len < id_prefix + 6 then
    cur_id_len := 32;
  end if;
  cur_max_id := nvl(cur_max_id, 0) + 1;
  if id_prefix is null then
    str_max_id := lpad(cur_max_id, cur_id_len, '0');
  else
    str_max_id := id_prefix || lpad(cur_max_id, cur_id_len - length(id_prefix), '0');
  end if;
  exec_sql := 'select count(*) from ' || in_table_name || ' where ' || cur_column_name || ' = ''' || str_max_id || '''';
  execute immediate exec_sql into row_count;
  if row_count > 0 then 
    exec_sql := 'select max(' || cur_column_name || ') from ' || in_table_name
      || ' where ' || cur_column_name || ' like ''' || id_prefix || '%'''
      || '   and nvl2(translate(substr(' || cur_column_name || ', length(''' || id_prefix || ''') + 1, 100), ''\1234567890'', ''\''), 0, 1) = 1';
    execute immediate exec_sql into str_max_id;
    cur_max_id := nvl(to_number(substr(str_max_id, length(id_prefix) + 1, 100)), 0) + 1;
    if id_prefix is null then
      str_max_id := lpad(cur_max_id, cur_id_len, '0');
    else
      str_max_id := id_prefix || lpad(cur_max_id, cur_id_len - length(id_prefix), '0');
    end if;
  end if;
  update sys_table set max_id = cur_max_id + return_count - 1 where upper(name) = upper(in_table_name) and upper(key_field_name) = upper(cur_column_name);
  commit;
  if in_return_type = 1 then
    return cur_max_id;
  end if;
  return str_max_id;
end create_table_id;
/

create or replace trigger sys_dataset_ud
  before update or delete
  on sys_dataset
  for each row
begin
  --�޸Ļ���ɾ�����ݼ��Զ���¼
  if deleting then
    insert into sys_operate_log(id, LOG_TYPE, LOG_LEVEL, REF_TABLE_NAME, REF_FIELD_NAME, REF_FIELD_VALUE,
        OPERATE, OPERATE_LOG)
      values(create_table_id('sys_operate_log'), 1, 0, 'sys_dataset', 'id,no,module_id', :old.id || ',' || :old.no || ',' || :old.module_id,
        'delete', :old.sql);
  elsif updating('sql') then
    insert into sys_operate_log(id, LOG_TYPE, LOG_LEVEL, REF_TABLE_NAME, REF_FIELD_NAME, REF_FIELD_VALUE,
        OPERATE, OPERATE_LOG)
      values(create_table_id('sys_operate_log'), 1, 0, 'sys_dataset', 'id,no,module_id', :old.id || ',' || :old.no || ',' || :old.module_id,
        'updateSql', :old.sql);
  end if;
end sys_dataset_ud;
/

create or replace trigger sys_module_ud
  before update or delete
  on sys_module
  for each row
begin
  --ɾ��ģ���Զ���¼
  if deleting then
    insert into sys_operate_log(id, LOG_TYPE, LOG_LEVEL, REF_TABLE_NAME, REF_FIELD_NAME, REF_FIELD_VALUE,
        OPERATE, OPERATE_LOG)
      values(create_table_id('sys_operate_log'), 1, 0, 'sys_module', 'id,no,name', :old.id || ',' || :old.no || ',' || :old.name,
        'delete', 'ɾ��ģ��');
  end if;
end sys_module_ud;
/

create or replace trigger sys_params_ud
  before update or delete
  on sys_params
  for each row
begin
  --�޸ġ�ɾ��ϵͳ�����Զ���¼
  if deleting then
    if :old.no = 'SYSTEM_PARAMS_ROOT' or :old.no = 'USER_PARAMS_ROOT' then
      RAISE_APPLICATION_ERROR(-20001, 'ϵͳ�������ڵ�����û��������ڵ㲻�ܱ�ɾ����');
    end if;
    insert into sys_operate_log(id, LOG_TYPE, LOG_LEVEL, REF_TABLE_NAME, REF_FIELD_NAME, REF_FIELD_VALUE,
        OPERATE, OPERATE_LOG)
      values(create_table_id('sys_operate_log'), 1, 0, 'sys_params', 'id,no,name', :old.id || ',' || :old.no || ',' || :old.name,
        'delete', :old.lvalue || chr(13) || :old.mvalue || chr(13) || :old.hvalue);
  elsif updating('no') then
    insert into sys_operate_log(id, LOG_TYPE, LOG_LEVEL, REF_TABLE_NAME, REF_FIELD_NAME, REF_FIELD_VALUE,
        OPERATE, OPERATE_LOG)
      values(create_table_id('sys_operate_log'), 1, 0, 'sys_params', 'id,no,name', :old.id || ',' || :old.no || ',' || :old.name,
        'updateNo', :old.no || chr(13) || :new.no);
  elsif updating('lvalue') then
    insert into sys_operate_log(id, LOG_TYPE, LOG_LEVEL, REF_TABLE_NAME, REF_FIELD_NAME, REF_FIELD_VALUE,
        OPERATE, OPERATE_LOG)
      values(create_table_id('sys_operate_log'), 1, 0, 'sys_params', 'id,no,name', :old.id || ',' || :old.no || ',' || :old.name,
        'updateLValue', :old.lvalue || chr(13) || :new.lvalue);
  elsif updating('mvalue') then
    insert into sys_operate_log(id, LOG_TYPE, LOG_LEVEL, REF_TABLE_NAME, REF_FIELD_NAME, REF_FIELD_VALUE,
        OPERATE, OPERATE_LOG)
      values(create_table_id('sys_operate_log'), 1, 0, 'sys_params', 'id,no,name', :old.id || ',' || :old.no || ',' || :old.name,
        'updateMValue', :old.mvalue || chr(13) || :new.mvalue);
  elsif updating('hvalue') then
    insert into sys_operate_log(id, LOG_TYPE, LOG_LEVEL, REF_TABLE_NAME, REF_FIELD_NAME, REF_FIELD_VALUE,
        OPERATE, OPERATE_LOG)
      values(create_table_id('sys_operate_log'), 1, 0, 'sys_params', 'id,no,name', :old.id || ',' || :old.no || ',' || :old.name,
        'updateMValue', :old.hvalue || chr(13) || :new.hvalue);
  end if;
end sys_params_ud;
/

create or replace trigger sys_user_ud
  before update or delete
  on sys_user
  for each row
begin
  --�޸Ļ���ɾ���û��Զ���¼
  if deleting then
    if lower(:old.user_name) in ('root', 'admin') then
      /*insert into sys_operate_log(id, LOG_TYPE, LOG_LEVEL, REF_TABLE_NAME, REF_FIELD_NAME, REF_FIELD_VALUE,
          OPERATE, OPERATE_LOG)
        values(create_table_id('sys_operate_log'), 1, 0, 'sys_user', 'id,user_name', :old.id || ',' || :old.user_name,
          'delete', 'try delete root or admin user, fail');*/
      RAISE_APPLICATION_ERROR(-20001, 'ϵͳȱʡ�û�������ɾ����');
    end if;
  elsif (updating('id') or updating('user_name'))and lower(:old.user_name) in ('root', 'admin') then
    RAISE_APPLICATION_ERROR(-20001, 'ϵͳȱʡ�û��������޸��û�����');
  end if;
end sys_user_ud;
/

/*************ҵ�����**********************************/
drop table trans_bidder_organ_rel;
drop table trans_organ_rel;
drop table trans_organ_bank_rel;
drop table trans_organ_business_rel;
drop table trans_multi_trade_rel;
drop table trans_license_step_rel;
drop table trans_transaction_type_rel;
drop table trans_business_type_rel;
drop table trans_transaction_type;
drop table trans_business_type;
drop table trans_goods_type;
drop table trans_multi_trade;
drop table trans_license_step;

create table trans_license_step(
  id varchar2(50) primary key,
  no varchar2(100),
  name varchar2(100),
  module_id varchar2(50),
  module_params varchar2(500),
  module_url varchar2(500),
  is_valid number(1) default(1),
  create_user_id varchar2(50),
  create_date date default(sysdate),
  remark varchar2(100)
);

alter table trans_license_step add constraint fk_trans_license_step foreign key(module_id)
  references sys_module(id) on delete set null;
create unique index ui_trans_license_step on trans_license_step(name) tablespace EPF_INDEX;

comment on table trans_license_step is '�������벽���ֵ��';
comment on column trans_license_step.no is '������';
comment on column trans_license_step.name is '��������';
comment on column trans_license_step.module_id is '����ģ��ID��sys_module.id';
comment on column trans_license_step.module_params is '����ģ���������������module_id���õ�ģ�����Ӻ�';
comment on column trans_license_step.module_url is 'ģ�����ӣ��д�ֵʱ����module_id����';
comment on column trans_license_step.is_valid is '1��Ч';
comment on column trans_license_step.create_user_id is '������ID,sys_user.id';
comment on column trans_license_step.create_date is '��������';
comment on column trans_license_step.remark is '��ע';

create table trans_multi_trade(
  id varchar2(50) primary key,
  no varchar2(100),
  name varchar2(100),
  class_type varchar2(100),
  unit varchar2(100),
  enter_flag_0 number(1) default(0),
  enter_flag_1 integer default(0),
  enter_flag_2 integer default(0),
  first_wait integer default(480),
  limit_wait integer default(300),
  last_wait integer default(10),
  is_valid number(1) default(1),
  create_user_id varchar2(50),
  create_date date default(sysdate),
  remark varchar2(100)
);

create unique index ui_trans_multi_trade on trans_multi_trade(name) tablespace EPF_INDEX;

comment on table trans_multi_trade is '��ָ���ֵ��';
comment on column trans_multi_trade.no is 'ָ����';
comment on column trans_multi_trade.name is 'ָ������';
comment on column trans_multi_trade.class_type is '�������';
comment on column trans_multi_trade.unit is '��λ';
comment on column trans_multi_trade.enter_flag_0 is '1�������߲��ܽ�����ʱ���ۣ���һ��ָ�꣩';
comment on column trans_multi_trade.enter_flag_1 is '������ʱ���ۣ���һ��ָ�꣩�������������';
comment on column trans_multi_trade.enter_flag_2 is '������ʱ���ۣ���һ��ָ�꣩����ĳ�������';
comment on column trans_multi_trade.first_wait is '����ȴ�ʱ�䣨�룩';
comment on column trans_multi_trade.limit_wait is '����ȴ�ʱ�䣨�룩';
comment on column trans_multi_trade.last_wait is '�����ȴ�ʱ�䣨�룩';
comment on column trans_multi_trade.is_valid is '1��Ч';
comment on column trans_multi_trade.create_user_id is '������ID,sys_user.id';
comment on column trans_multi_trade.create_date is '��������';
comment on column trans_multi_trade.remark is '��ע';

create table trans_goods_type(
  id varchar2(50) primary key,
  no varchar2(100),
  name varchar2(100),
  goods_name varchar2(100),
  no_label varchar2(100),
  name_label varchar2(100),
  use_label varchar2(100),
  edit_goods_module_id varchar2(500),
  edit_goods_module_params varchar2(500),
  edit_goods_module_url varchar2(500),
  view_goods_module_id varchar2(500),
  view_goods_module_params varchar2(500),
  view_goods_module_url varchar2(500),
  is_valid number(1) default(1),
  create_user_id varchar2(50),
  create_date date default(sysdate),
  remark varchar2(100)
);

create unique index ui_trans_goods_type on trans_goods_type(name) tablespace EPF_INDEX;
alter table trans_goods_type add constraint fk_trans_goods_type foreign key(edit_goods_module_id)
  references sys_module(id) on delete set null;
alter table trans_goods_type add constraint fk_trans_goods_type1 foreign key(view_goods_module_id)
  references sys_module(id) on delete set null;

comment on table trans_goods_type is '���������ͱ�';
comment on column trans_goods_type.no is '���';
comment on column trans_goods_type.name is '����';
comment on column trans_goods_type.goods_name is '����������';
comment on column trans_goods_type.no_label is '��ı����ʾ��ǩ���������Ĳ���ʾ����ֶ�';
comment on column trans_goods_type.name_label is '���������ʾ��ǩ���������Ĳ���ʾ�����ֶ�';
comment on column trans_goods_type.use_label is '�����;��ʾ��ǩ';
comment on column trans_goods_type.edit_goods_module_id is '�༭����������ҳ��ģ��ID��sys_module.id';
comment on column trans_goods_type.edit_goods_module_params is '�༭����������ҳ��ģ���������������edit_goods_module_id���õ�ģ�����Ӻ�';
comment on column trans_goods_type.edit_goods_module_url is '�༭����������ҳ��ģ�����ӣ��д�ֵʱ����edit_goods_module_id����';
comment on column trans_goods_type.view_goods_module_id is '�鿴����������ҳ��ģ��ID��sys_module.id';
comment on column trans_goods_type.view_goods_module_params is '�鿴����������ҳ��ģ���������������view_goods_module_id���õ�ģ�����Ӻ�';
comment on column trans_goods_type.view_goods_module_url is '�鿴����������ҳ��ģ�����ӣ��д�ֵʱ����view_goods_module_id����';
comment on column trans_goods_type.is_valid is '1��Ч';
comment on column trans_goods_type.create_user_id is '������ID,sys_user.id';
comment on column trans_goods_type.create_date is '��������';
comment on column trans_goods_type.remark is '��ע';

create table trans_business_type(
  id varchar2(50) primary key,
  no varchar2(100),
  name varchar2(100),
  is_valid number(1) default(1),
  create_user_id varchar2(50),
  create_date date default(sysdate),
  remark varchar2(100)
);

create unique index ui_trans_business_type on trans_business_type(name) tablespace EPF_INDEX;

comment on table trans_business_type is 'ҵ�����ͱ�';
comment on column trans_business_type.no is '���';
comment on column trans_business_type.name is '����';
comment on column trans_business_type.is_valid is '1��Ч';
comment on column trans_business_type.create_user_id is '������ID,sys_user.id';
comment on column trans_business_type.create_date is '��������';
comment on column trans_business_type.remark is '��ע';

create table trans_transaction_type(
  id varchar2(50) primary key,
  no varchar2(100),
  name varchar2(100),
  trans_type number(1) default(0),
  is_net_trans number(1) default(1),
  is_limit_trans number(1) default(0),
  allow_live number(1) default(1),
  allow_union number(1) default(1),
  allow_trust number(1) default(0),
  allow_multi_trade number(1) default(0),
  end_notice_time integer default(30),
  end_list_time integer default(20),
  end_focus_time integer default(20),
  enter_flag_0 number(1) default(0),
  enter_flag_1 integer default(0),
  enter_flag_2 integer default(0),
  is_valid number(1) default(1),
  create_user_id varchar2(50),
  create_date date default(sysdate),
  remark varchar2(100)
);

create unique index ui_trans_transaction_type on trans_transaction_type(name) tablespace EPF_INDEX;

comment on table trans_transaction_type is '���׷�ʽ��';
comment on column trans_transaction_type.no is '���׷�ʽ���';
comment on column trans_transaction_type.name is '���׷�ʽ����';
comment on column trans_transaction_type.trans_type is '�������ͣ�ͬtrans_target��0���ƣ�1������2�б�';
comment on column trans_transaction_type.is_net_trans is '0�ֳ����ף�1���Ͻ��ף�2�ֳ�������ͬ������';
comment on column trans_transaction_type.is_limit_trans is '1���Ʊ���ڼ��б����ں���Ƿ���Ҫ������ʱ����';
comment on column trans_transaction_type.allow_live is '1����ֱ��';
comment on column trans_transaction_type.allow_union is '1�������Ͼ���';
comment on column trans_transaction_type.allow_trust is '1����ί�б���';
comment on column trans_transaction_type.allow_multi_trade is '1�����ָ��';
comment on column trans_transaction_type.end_notice_time is '����ʱ��';
comment on column trans_transaction_type.end_list_time is '����ʱ��';
comment on column trans_transaction_type.end_focus_time is '���Ʊ��ۣ����б����ڣ�ʱ��';
comment on column trans_transaction_type.enter_flag_0 is '1�������߲��ܽ�����ʱ���ۣ���һ��ָ�꣩';
comment on column trans_transaction_type.enter_flag_1 is '������ʱ���ۣ���һ��ָ�꣩�������������';
comment on column trans_transaction_type.enter_flag_2 is '������ʱ���ۣ���һ��ָ�꣩����ĳ�������';
comment on column trans_transaction_type.is_valid is '1��Ч';
comment on column trans_transaction_type.create_user_id is '������ID,sys_user.id';
comment on column trans_transaction_type.create_date is '��������';
comment on column trans_transaction_type.remark is '��ע';

create table trans_business_type_rel(
  id varchar2(50) primary key,
  goods_type_id varchar2(50),
  business_type_id varchar2(50),
  name varchar2(100),
  is_valid number(1) default(1),
  create_user_id varchar2(50),
  create_date date default(sysdate),
  remark varchar2(100)
);

alter table trans_business_type_rel add constraint fk_trans_business_type_rel foreign key(goods_type_id)
  references trans_goods_type(id) on delete cascade;
alter table trans_business_type_rel add constraint fk_trans_business_type_rel1 foreign key(business_type_id)
  references trans_business_type(id);
create unique index ui_trans_business_type_rel on trans_business_type_rel(goods_type_id, business_type_id) tablespace EPF_INDEX;

comment on table trans_business_type_rel is '��������������ҵ������';
comment on column trans_business_type_rel.goods_type_id is '����������ID��trans_goods_type.id';
comment on column trans_business_type_rel.business_type_id is 'ҵ������ID��trans_business_type.id';
comment on column trans_business_type_rel.name is 'ҵ��������������Ϊ��ʱʹ��trans_business_type.name';
comment on column trans_business_type_rel.is_valid is '1��Ч';
comment on column trans_business_type_rel.create_user_id is '������ID,sys_user.id';
comment on column trans_business_type_rel.create_date is '��������';
comment on column trans_business_type_rel.remark is '��ע';

create table trans_transaction_type_rel(
  id varchar2(50) primary key,
  business_type_rel_id varchar2(50),
  trans_type_id varchar2(50),
  name varchar2(100),
  is_valid number(1) default(1),
  create_user_id varchar2(50),
  create_date date default(sysdate),
  remark varchar2(100)
);

alter table trans_transaction_type_rel add constraint fk_trans_transaction_type_rel foreign key(business_type_rel_id)
  references trans_business_type_rel(id) on delete cascade;
alter table trans_transaction_type_rel add constraint fk_trans_transaction_type_rel1 foreign key(trans_type_id)
  references trans_transaction_type(id);
create unique index ui_trans_transaction_type_rel on trans_transaction_type_rel(business_type_id, trans_type_id) tablespace EPF_INDEX;

comment on table trans_transaction_type_rel is 'ҵ������������׷�ʽ';
comment on column trans_transaction_type_rel.business_type_rel_id is 'ҵ�����ID��trans_business_type_rel.id';
comment on column trans_transaction_type_rel.trans_type_id is '���׷�ʽID��trans_transaction_type.id';
comment on column trans_transaction_type_rel.name is '���׷�ʽ��������Ϊ��ʱʹ��trans_transaction_type.name';
comment on column trans_transaction_type_rel.is_valid is '1��Ч';
comment on column trans_transaction_type_rel.create_user_id is '������ID,sys_user.id';
comment on column trans_transaction_type_rel.create_date is '��������';
comment on column trans_transaction_type_rel.remark is '��ע';

create table trans_license_step_rel(
  id varchar2(50) primary key,
  trans_type_rel_id varchar2(50),
  license_step_id varchar2(50),
  name varchar2(100),
  is_valid number(1) default(1),
  create_user_id varchar2(50),
  create_date date default(sysdate),
  remark varchar2(100)
);

alter table trans_license_step_rel add constraint fk_trans_license_step_rel foreign key(trans_type_rel_id)
  references trans_transaction_type_rel(id) on delete cascade;
alter table trans_license_step_rel add constraint fk_trans_license_step_rel1 foreign key(license_step_id)
  references trans_license_step(id);
create unique index ui_trans_license_step_rel on trans_license_step_rel(trans_type_id, license_step_id) tablespace EPF_INDEX;

comment on table trans_license_step_rel is '���׷�ʽ�����������벽��';
comment on column trans_license_step_rel.trans_type_rel_id is '���׷�ʽID��trans_transaction_type_rel.id';
comment on column trans_license_step_rel.license_step_id is '�������벽��ID��trans_license_step.id';
comment on column trans_license_step_rel.name is '�������벽����������Ϊ��ʱʹ��trans_license_step.name';
comment on column trans_license_step_rel.is_valid is '1��Ч';
comment on column trans_license_step_rel.create_user_id is '������ID,sys_user.id';
comment on column trans_license_step_rel.create_date is '��������';
comment on column trans_license_step_rel.remark is '��ע';

create table trans_multi_trade_rel(
  id varchar2(50) primary key,
  trans_type_rel_id varchar2(50),
  multi_trade_id varchar2(50),
  name varchar2(100),
  is_valid number(1) default(1),
  create_user_id varchar2(50),
  create_date date default(sysdate),
  remark varchar2(100)
);

alter table trans_multi_trade_rel add constraint fk_trans_multi_trade_rel foreign key(trans_type_rel_id)
  references trans_transaction_type_rel(id) on delete cascade;
alter table trans_multi_trade_rel add constraint fk_trans_multi_trade_rel1 foreign key(multi_trade_id)
  references trans_multi_trade(id);
create unique index ui_trans_multi_trade_rel on trans_multi_trade_rel(trans_type_id, multi_trade_id) tablespace EPF_INDEX;

comment on table trans_multi_trade_rel is '���׷�ʽ������ָ��';
comment on column trans_multi_trade_rel.trans_type_id is '���׷�ʽID��trans_transaction_type.id';
comment on column trans_multi_trade_rel.multi_trade_id is '��ָ��ID��trans_multi_trade.id';
comment on column trans_multi_trade_rel.name is '��ָ����������Ϊ��ʱʹ��trans_multi_trade.name';
comment on column trans_multi_trade_rel.is_valid is '1��Ч';
comment on column trans_multi_trade_rel.create_user_id is '������ID,sys_user.id';
comment on column trans_multi_trade_rel.create_date is '��������';
comment on column trans_multi_trade_rel.remark is '��ע';

create table trans_organ_business_rel(
  id varchar2(50) primary key,
  organ_id varchar2(50),
  business_type_rel_id varchar2(50),
  name varchar2(100),
  is_valid number(1) default(1),
  create_user_id varchar2(50),
  create_date date default(sysdate),
  remark varchar2(100)
);

alter table trans_organ_business_rel add constraint fk_trans_organ_business_rel foreign key(organ_id)
  references sys_organ(id) on delete cascade;
alter table trans_organ_business_rel add constraint fk_trans_organ_business_rel1 foreign key(business_type_rel_id)
  references trans_business_type_rel(id);
create unique index ui_trans_organ_business_rel on trans_organ_business_rel(organ_id, business_type_rel_id) tablespace EPF_INDEX;

comment on table trans_organ_business_rel is '��λ֧�ֵ�ҵ������';
comment on column trans_organ_business_rel.organ_id is '��λID��sys_organ.id';
comment on column trans_organ_business_rel.business_type_rel_id is 'ҵ�����͹���ID��trans_business_type_rel.id';
comment on column trans_organ_business_rel.is_valid is '1��Ч';
comment on column trans_organ_business_rel.create_user_id is '������ID,sys_user.id';
comment on column trans_organ_business_rel.create_date is '��������';
comment on column trans_organ_business_rel.remark is '��ע';

/***************************************/

create table trans_organ_bank_rel(
  id varchar2(50) primary key,
  organ_id varchar2(50),
  bank_id varchar2(50),
  ref_type number(1) default(0),
  is_valid number(1) default(1),
  create_user_id varchar2(50),
  create_date date default(sysdate),
  remark varchar2(100)
);

alter table trans_organ_bank_rel add constraint fk_trans_organ_bank_rel foreign key(organ_id)
  references sys_organ(id) on delete cascade;
alter table trans_organ_bank_rel add constraint fk_trans_organ_bank_rel1 foreign key(bank_id)
  references trans_bank(id);
create unique index ui_trans_organ_bank_rel on trans_organ_bank_rel(organ_id, bank_id, ref_type) tablespace EPF_INDEX;

comment on table trans_organ_bank_rel is '���׵�λ�����й�������������ʱ�����ڴ˱�Χ��ѡ�񽻿�����';
comment on column trans_organ_bank_rel.organ_id is '��λID��sys_organ.id';
comment on column trans_organ_bank_rel.bank_id is '����ID��trans_bank.id';
comment on column trans_organ_bank_rel.ref_type is '�������͡�0����λֱ���Ľ������У�1���������׵�λ�Ľ������У�����λ����';
comment on column trans_organ_bank_rel.is_valid is '1��Ч';
comment on column trans_organ_bank_rel.create_user_id is '������ID,sys_user.id';
comment on column trans_organ_bank_rel.create_date is '��������';
comment on column trans_organ_bank_rel.remark is '��ע';

create table trans_notice_account_rel(
  id varchar2(50) primary key,
  notice_id varchar2(50),
  account_id varchar2(50),
  is_valid number(1) default(1),
  create_user_id varchar2(50),
  create_date date default(sysdate),
  remark varchar2(100)
);

alter table trans_notice_account_rel add constraint fk_trans_notice_account_rel foreign key(notice_id)
  references trans_notice(id) on delete cascade;
alter table trans_notice_account_rel add constraint fk_trans_notice_account_rel1 foreign key(account_id)
  references trans_account(id);
create unique index ui_trans_notice_account_rel on trans_notice_account_rel(notice_id, account_id) tablespace EPF_INDEX;

comment on table trans_notice_account_rel is '���淢����ѡ�˺ţ���trans_organ_bank_relѡ�������˺ţ���������ʱ����ʾ������˺ű�ѡ';
comment on column trans_notice_account_rel.notice_id is '����ID��trans_notice.id';
comment on column trans_notice_account_rel.account_id is '���˺�ID��trans_account.id';
comment on column trans_notice_account_rel.is_valid is '1��Ч';
comment on column trans_notice_account_rel.create_user_id is '������ID,sys_user.id';
comment on column trans_notice_account_rel.create_date is '��������';
comment on column trans_notice_account_rel.remark is '��ע';

create table trans_organ_rel(
  id varchar2(50) primary key,
  organ_id varchar2(50),
  ref_organ_id varchar2(50),
  ref_type number(1) default(0),
  is_valid number(1) default(1),
  create_user_id varchar2(50),
  create_date date default(sysdate),
  remark varchar2(100)
);

alter table trans_organ_rel add constraint fk_trans_organ_rel foreign key(organ_id)
  references sys_organ(id) on delete cascade;
alter table trans_organ_rel add constraint fk_trans_organ_rel1 foreign key(ref_organ_id)
  references sys_organ(id);
create unique index ui_trans_organ_rel on trans_organ_rel(organ_id, ref_organ_id, ref_type) tablespace EPF_INDEX;

comment on table trans_organ_rel is '���׵�λ����';
comment on column trans_organ_rel.organ_id is '��λID��sys_organ.id';
comment on column trans_organ_rel.ref_organ_id is '�������õ�λID��sys_organ.id';
comment on column trans_organ_rel.ref_type is '�������0ref_organ_idΪorgan_id��ί�еĽ��׵�λ��1ref_organ_idΪorgan_id�ɲ�ѯĿ�굥λ��ģ�2ref_organ_idΪorgan_id���޸�Ŀ�굥λ���';
comment on column trans_organ_rel.is_valid is '1��Ч';
comment on column trans_organ_rel.create_user_id is '������ID,sys_user.id';
comment on column trans_organ_rel.create_date is '��������';
comment on column trans_organ_rel.remark is '��ע';

create table trans_bidder_organ_rel(
  id varchar2(50) primary key,
  bidder_id varchar2(50),
  organ_id varchar2(50),
  ref_type number(2) default(0),
  is_valid number(1) default(1),
  create_user_id varchar2(50),
  create_date date default(sysdate),
  remark varchar2(100)
);

alter table trans_bidder_organ_rel add constraint fk_trans_bidder_organ_rel foreign key(bidder_id)
  references trans_bidder(id) on delete cascade;
alter table trans_bidder_organ_rel add constraint fk_trans_bidder_organ_rel1 foreign key(organ_id)
  references sys_organ(id);
create unique index ui_trans_bidder_organ_rel on trans_bidder_organ_rel(bidder_id, organ_id, ref_type) tablespace EPF_INDEX;

comment on table trans_bidder_organ_rel is '�������뽻�׵�λ�����������������Ʋ�ѯ��Щ��λ�ı�Ĳ��ύ�������룬�������ϵͳȱʡ��ʽ';
comment on column trans_bidder_organ_rel.bidder_id is '������ID��trans_bidder.id';
comment on column trans_bidder_organ_rel.organ_id is '��λID��sys_organ.id';
comment on column trans_bidder_organ_rel.ref_type is '�������0bidder_id�ɲ�ѯ������organ_id��λ¼��ı��(������)��1bidder_id�ɲ�ѯ�����뽻�׵�λorgan_id�����ı��(������)��10bidder_id���ɲ�ѯ������organ_id��λ¼��ı��(������)��11bidder_id���ɲ�ѯ�����뽻�׵�λorgan_id�����ı��(������)';
comment on column trans_bidder_organ_rel.is_valid is '1��Ч';
comment on column trans_bidder_organ_rel.create_user_id is '������ID,sys_user.id';
comment on column trans_bidder_organ_rel.create_date is '��������';
comment on column trans_bidder_organ_rel.remark is '��ע';

create table trans_target_earnest_money(
  id varchar2(50) primary key,
  target_id varchar2(50),
  currency varchar2(50),
  amount number(18, 2),
  is_valid number(1) default(1),
  create_user_id varchar2(50),
  create_date date default(sysdate),
  remark varchar2(100)
);

alter table trans_target_earnest_money add constraint fk_trans_target_earnest_money foreign key(target_id)
  references trans_target(id) on delete cascade;
create unique index ui_trans_target_earnest_money on trans_target_earnest_money(target_id, currency) tablespace EPF_INDEX;

comment on table trans_target_earnest_money is '��ı�֤���';
comment on column trans_target_earnest_money.target_id is '���ID��trans_target.id';
comment on column trans_target_earnest_money.currency is '����';
comment on column trans_target_earnest_money.amount is '��֤����';
comment on column trans_target_earnest_money.is_valid is '1��Ч';
comment on column trans_target_earnest_money.create_user_id is '������ID,sys_user.id';
comment on column trans_target_earnest_money.create_date is '��������';
comment on column trans_target_earnest_money.remark is '��ע';

create table trans_role_goods_type_rel(
  id varchar2(50) primary key,
  role_id varchar2(50),
  goods_type_id varchar2(50),
  is_valid number(1) default(1),
  create_user_id varchar2(50),
  create_date date default(sysdate),
  remark varchar2(100)
);

alter table trans_role_goods_type_rel add constraint fk_trans_role_goods_type_rel foreign key(role_id)
  references sys_role(id) on delete cascade;
alter table trans_role_goods_type_rel add constraint fk_trans_role_goods_type_rel1 foreign key(goods_type_id)
  references trans_goods_type(id);
create unique index ui_trans_role_goods_type_rel on trans_role_goods_type_rel(role_id, goods_type_id) tablespace EPF_INDEX;

comment on table trans_role_goods_type_rel is '��ɫ�ܲ����Ľ���������';
comment on column trans_role_goods_type_rel.role_id is '��ɫID��sys_role.id';
comment on column trans_role_goods_type_rel.goods_type_id is '����������ID��trans_goods_type.id';
comment on column trans_role_goods_type_rel.is_valid is '1��Ч';
comment on column trans_role_goods_type_rel.create_user_id is '������ID,sys_user.id';
comment on column trans_role_goods_type_rel.create_date is '��������';
comment on column trans_role_goods_type_rel.remark is '��ע';

alter table sys_organ add status number(1) default(0);
alter table sys_organ add is_trans_organ number(1) default(0);
comment on column sys_organ.status is '״̬��0�����У�1�Ѽ���(��״̬Ϊ��������״̬)��2���ᣬ3������4ע��';
comment on column sys_organ.is_trans_organ is '�Ƿ��׵�λ��1���׵�λ';
alter table trans_target add trans_organ_id varchar2(50);
comment on column trans_target.trans_organ_id is '��Ľ��׵�λID��ĳЩ��λ�޽���ְ�ܣ��õ�λ����ί���������׵�λ���н��ס������ʾ�ھ��������ڵĵ�λ����';
ALTER TABLE TRANS_ACCOUNT MODIFY BUSINESS_TYPE VARCHAR2(50);
ALTER TABLE TRANS_ACCOUNT_BILL MODIFY BUSINESS_TYPE VARCHAR2(50);
ALTER TABLE TRANS_BIDDER_PRIORITY MODIFY BUSINESS_TYPE VARCHAR2(50);
ALTER TABLE TRANS_NOTICE MODIFY BUSINESS_TYPE VARCHAR2(50);
ALTER TABLE TRANS_TARGET MODIFY BUSINESS_TYPE VARCHAR2(50);
ALTER TABLE TRANS_TRUST MODIFY BUSINESS_TYPE VARCHAR2(50);
comment on column TRANS_ACCOUNT.BUSINESS_TYPE is 'ҵ�����TRANS_BUSINESS_TYPE_REL.id';
comment on column TRANS_ACCOUNT_BILL.BUSINESS_TYPE is 'ҵ�����TRANS_BUSINESS_TYPE_REL.id';
comment on column TRANS_BIDDER_PRIORITY.BUSINESS_TYPE is 'ҵ�����TRANS_BUSINESS_TYPE_REL.id';
comment on column TRANS_NOTICE.BUSINESS_TYPE is 'ҵ�����TRANS_BUSINESS_TYPE_REL.id';
comment on column TRANS_TARGET.BUSINESS_TYPE is 'ҵ�����TRANS_BUSINESS_TYPE_REL.id';
comment on column TRANS_TRUST.BUSINESS_TYPE is 'ҵ�����TRANS_BUSINESS_TYPE_REL.id';
alter table trans_target drop column EARNEST_MONEY;

/*�����ʸ����begin*************************************************************************
1��ע��������¼��ַ�ʽ�����Կ�������ϵͳѡ��ĳ�ַ�ʽ������ָ��ĳ��ҵ��������ĳ��ע�᷽ʽ��
    .����ע�ᣬ�Զ�����KEY���󶨣�
    .����ע���ύ���ϣ����а���KEY�������ڽ�����˲��ϼ���KEY��
    .���а���KEY��������ע�ᡢ��˲��ϣ��Լ���KEY��
    ע��ʱ���ϴ����Ӽ���Ϊ���������ϡ�
2���ʸ������Ҫ��Ϊ�������ݣ�
    .ע�Ὰ����������ˣ���Ҫ�Ǽ�龺�����ύ��ע�����Ϻ�ʵ�ʵ������Ƿ�һ�£�������Ȼ�˵����֤�����߷��˵���֯�������롢Ӫҵִ�ա�������Ϣ�ȡ�Ŀǰ�������пƱ��е��ⲿ�ֹ�����
    .�����˵ĳ�����ص���ˣ������Ƿ����������ء�Ƿ�ؼۿ�ȷ�������ݣ�
    .�����ڲ�ҵ�û����ʸ���ˣ����ĳ��ĵľ����ʸ�������
    ��һ�������ע����ˣ������Ǿ�������ʱ���ʸ���ˡ�
3���ʸ���˿ɷ�Ϊǰ����ˡ�������ˡ�ǰ�ü�������ˡ�
4���ڶ�������ṩ������嵥�ֶα�Ϊʵ�����ʱ�ṩ��Ҫ��˵��嵥�����ʱʵ��������������ύ������Ƿ�ͨ������ע������ϴ�������������ע��ʱ���ύһЩ���Ӳ��ϣ�������Ϊĳ����ҵ�񡢱��������Ҫǰ�á����û���ǰ���ã�ǰ�����ø���Ҫ��˼��ʲô��������ֵ����ѡ�񣩣�
5�����Կ����Ժ󼯳ɳ�����ص��ⲿϵͳ���߱��ṩ�ڶ�����˵Ĳ���������Զ���ɣ�

���ݿ��޸Ľű���
******************************************************************************************/
alter table trans_bidder add is_self_regist number(1) default(0);
comment on column trans_bidder.is_self_regist is '1��������ע�����ϵľ����ˡ�statusӦ�õ���0�����ύ���Ͻ������';

alter table trans_target add confirm_mode number(1) default(0);
comment on column trans_target.confirm_mode is '��ľ��������ʸ���˷�ʽ��0����Ҫ��ˣ�1ǰ����ˣ�2������ˣ�3ǰ�������';
comment on column trans_target.status is '״̬��0������(¼��)��1�����(����������)��2����ˣ�3�ѹ��棬4�����У�5Ԥ�ɽ�(����������ʱ����confirm_mode=2or3������ֱ�ӳɽ�6)��6�ѳɽ���7δ�ɽ�(����)';

create table trans_confirm_dict(
  id varchar2(50) primary key,
  no varchar2(200),
  name varchar2(200),
  is_valid number(1) default(1),
  create_user_id varchar2(50),
  create_date date default(sysdate),
  remark varchar2(100)
);

create unique index ui_trans_confirm_dict on trans_confirm_dict(name) tablespace EPF_INDEX;

comment on table trans_confirm_dict is '���������ʸ�������ֵ�����';
comment on column trans_confirm_dict.no is '���';
comment on column trans_confirm_dict.name is '����';
comment on column trans_confirm_dict.is_valid is '1��Ч';
comment on column trans_confirm_dict.create_user_id is '������ID,sys_user.id';
comment on column trans_confirm_dict.create_date is '��������';
comment on column trans_confirm_dict.remark is '��ע';

create table trans_confirm_dict_dtl(
  id varchar2(50) primary key,
  dict_id varchar2(50) not null,
  no varchar2(200),
  name varchar2(200),
  required_confirm number(1) default(0),
  required_opinion number(1) default(0),
  required_attach number(1) default(0),
  is_valid number(1) default(1),
  create_user_id varchar2(50),
  create_date date default(sysdate),
  remark varchar2(100)
);

alter table trans_confirm_dict_dtl add constraint fk_trans_confirm_dict_dtl foreign key(dict_id)
  references trans_confirm_dict(id) on delete cascade;
create unique index ui_trans_confirm_dict_dtl on trans_confirm_dict_dtl(dict_id, name) tablespace EPF_INDEX;

comment on table trans_confirm_dict_dtl is '���������ʸ�������ֵ�ӱ�';
comment on column trans_confirm_dict_dtl.dict_id is '����ID��trans_confirm_dict.id';
comment on column trans_confirm_dict_dtl.no is '���';
comment on column trans_confirm_dict_dtl.name is '����';
comment on column trans_confirm_dict_dtl.required_confirm is '1���ʱ����ͨ��';
comment on column trans_confirm_dict_dtl.required_opinion is '1���ʱ������������';
comment on column trans_confirm_dict_dtl.required_attach is '1���ʱ�����ϴ���ظ���';
comment on column trans_confirm_dict_dtl.is_valid is '1��Ч';
comment on column trans_confirm_dict_dtl.create_user_id is '������ID,sys_user.id';
comment on column trans_confirm_dict_dtl.create_date is '��������';
comment on column trans_confirm_dict_dtl.remark is '��ע';

create table trans_business_confirm_dict(
  id varchar2(50) primary key,
  business_type_rel_id varchar2(50),
  dict_dtl_id varchar2(50),
  confirm_mode number(1) default(1),
  is_valid number(1) default(1),
  create_user_id varchar2(50),
  create_date date default(sysdate),
  remark varchar2(100)
);

alter table trans_business_confirm_dict add constraint fk_trans_business_confirm_dict foreign key(business_type_rel_id)
  references trans_business_type_rel(id) on delete cascade;
alter table trans_business_confirm_dict add constraint fk_trans_business_confirm_dict1 foreign key(dict_dtl_id)
  references trans_confirm_dict_dtl(id);
create unique index ui_trans_business_confirm_dict on trans_business_confirm_dict(business_type_rel_id, dict_dtl_id, confirm_mode) tablespace EPF_INDEX;

comment on table trans_business_confirm_dict is 'ҵ��������ʸ���˶����';
comment on column trans_business_confirm_dict.business_type_rel_id is 'ҵ������ID��trans_business_type_rel.id';
comment on column trans_business_confirm_dict.dict_dtl_id is '������ID��trans_confirm_dict_dtl.id';
comment on column trans_business_confirm_dict.confirm_mode is '0ǰ�������������';
comment on column trans_business_confirm_dict.is_valid is '1��Ч';
comment on column trans_business_confirm_dict.create_user_id is '������ID,sys_user.id';
comment on column trans_business_confirm_dict.create_date is '��������';
comment on column trans_business_confirm_dict.remark is '��ע';

create table trans_target_confirm_dict(
  id varchar2(50) primary key,
  target_id varchar2(50),
  dict_dtl_id varchar2(50),
  confirm_mode number(1) default(1),
  is_valid number(1) default(1),
  create_user_id varchar2(50),
  create_date date default(sysdate),
  remark varchar2(100)
);

alter table trans_target_confirm_dict add constraint fk_trans_target_confirm_dict foreign key(target_id)
  references trans_target(id) on delete cascade;
alter table trans_target_confirm_dict add constraint fk_trans_target_confirm_dict1 foreign key(dict_dtl_id)
  references trans_confirm_dict_dtl(id);
create unique index ui_trans_target_confirm_dict on trans_target_confirm_dict(target_id, dict_dtl_id, confirm_mode) tablespace EPF_INDEX;

comment on table trans_target_confirm_dict is '��ľ����ʸ���˶����';
comment on column trans_target_confirm_dict.target_id is '���ID��trans_target.id';
comment on column trans_target_confirm_dict.dict_dtl_id is '������ID��trans_confirm_dict_dtl.id';
comment on column trans_target_confirm_dict.confirm_mode is '0ǰ�������������';
comment on column trans_target_confirm_dict.is_valid is '1��Ч';
comment on column trans_target_confirm_dict.create_user_id is '������ID,sys_user.id';
comment on column trans_target_confirm_dict.create_date is '��������';
comment on column trans_target_confirm_dict.remark is '��ע';

create table trans_license_confirm(
  id varchar2(50) primary key,
  license_id varchar2(50),
  confirm_mode number(1) default(0),
  confirmed number(1) default(0),
  confirmed_opinion varchar2(4000),
  confirmed_date date default(sysdate),
  is_valid number(1) default(1),
  create_user_id varchar2(50),
  create_date date default(sysdate),
  remark varchar2(100)
);

alter table trans_license_confirm add constraint fk_trans_license_confirm foreign key(license_id)
  references trans_license(id) on delete cascade;
create unique index ui_trans_license_confirm on trans_license_confirm(license_id, confirm_mode) tablespace EPF_INDEX;

comment on table trans_license_confirm is '���������ʸ������ϸ����������trans_business_confirm_dict��trans_target_confirm_dict';
comment on column trans_license_confirm.license_id is '��������ID��trans_license.id';
comment on column trans_license_confirm.confirm_mode is '0ǰ����ˣ�1������ˣ�2�����ʸ����';
comment on column trans_license_confirm.confirmed is '1���ͨ��';
comment on column trans_license_confirm.confirmed_opinion is '������';
comment on column trans_license_confirm.confirmed_date is '�������';
comment on column trans_license_confirm.is_valid is '1��Ч';
comment on column trans_license_confirm.create_user_id is '������ID,sys_user.id';
comment on column trans_license_confirm.create_date is '��������';
comment on column trans_license_confirm.remark is '��ע';

create table trans_license_confirm_dtl(
  id varchar2(50) primary key,
  confirm_id varchar2(50),
  dict_dtl_id varchar2(50),
  no varchar2(200),
  name varchar2(200),
  required_confirm number(1) default(0),
  required_opinion number(1) default(0),
  required_attach number(1) default(0),
  confirmed number(1) default(0),
  confirmed_opinion varchar2(4000),
  confirmed_date date default(sysdate),
  is_valid number(1) default(1),
  create_user_id varchar2(50),
  create_date date default(sysdate),
  remark varchar2(100)
);

alter table trans_license_confirm_dtl add constraint fk_trans_license_confirm_dtl foreign key(confirm_id)
  references trans_license_confirm(id) on delete cascade;
alter table trans_license_confirm_dtl add constraint fk_trans_license_confirm_dtl1 foreign key(dict_dtl_id)
  references trans_confirm_dict_dtl(id);
create unique index ui_trans_license_confirm_dtl on trans_license_confirm_dtl(confirm_id, dict_dtl_id) tablespace EPF_INDEX;

comment on table trans_license_confirm_dtl is '���������ʸ������ϸ����������trans_business_confirm_dict��trans_target_confirm_dict';
comment on column trans_license_confirm_dtl.confirm_id is '����ID��trans_license_confirm.id';
comment on column trans_license_confirm_dtl.dict_dtl_id is '������ID��trans_confirm_dict_dtl.id';
comment on column trans_license_confirm_dtl.no is 'ʵ�����������';
comment on column trans_license_confirm_dtl.name is 'ʵ�������������';
comment on column trans_license_confirm_dtl.required_confirm is 'ʵ����������Ƿ�������ͨ��';
comment on column trans_license_confirm_dtl.required_opinion is 'ʵ����������Ƿ����������';
comment on column trans_license_confirm_dtl.required_attach is 'ʵ����������Ƿ�����ϴ�����';
comment on column trans_license_confirm_dtl.confirmed is '1���ͨ��';
comment on column trans_license_confirm_dtl.confirmed_opinion is '������';
comment on column trans_license_confirm_dtl.confirmed_date is '�������';
comment on column trans_license_confirm_dtl.is_valid is '1��Ч';
comment on column trans_license_confirm_dtl.create_user_id is '������ID,sys_user.id';
comment on column trans_license_confirm_dtl.create_date is '��������';
comment on column trans_license_confirm_dtl.remark is '��ע';
/*�����ʸ����end***************************************************************************/
