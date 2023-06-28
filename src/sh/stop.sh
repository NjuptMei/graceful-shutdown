#!/bin/bash

# 检查是否存在 cims 用户
if id "esadmin" >/dev/null 2>&1; then
  esadmin_home=$(eval echo "~esadmin")
  # 切换到 cims 用户
  if [ -f "$esadmin_home/.bashrc" ]; then
    # 检查是否存在 RUN_STATE 环境变量
    if grep -q "RUN_STATUS=" "$esadmin_home/.bashrc"; then
      # 存在 RUN_STATE 环境变量，将其值修改为 "maintain"
	  current_run_status=$(grep "RUN_STATUS=" "$esadmin_home/.bashrc" | cut -d'=' -f2-)
	  if [[ "$current_run_status" != "maintain" ]]; then
		sed -i 's/RUN_STATUS=.*/RUN_STATUS=maintain/g' "$esadmin_home/.bashrc"
	  fi	
    else
      # 不存在 RUN_STATE 环境变量，新增环境变量设置
      echo "export RUN_STATUS=\"maintain\"" >> "$esadmin_home/.bashrc"
    fi
  else
    echo "无法找到 ~/.bashrc 文件"
    exit 1
  fi
  source "$esadmin_home/.bashrc"
  echo "app状态已修改，等待2min后执行停机"
  sleep 60

  # 要查找和终止的进程名称
  process_name="graceful-shutdown"

  # 查找进程号
  process_id=$(pgrep -f "$process_name")

  if [[ -n $process_id ]]; then
    echo "找到进程：$process_name (PID: $process_id)"

    # 终止进程
    kill -15 $process_id
    echo "进程已终止"
  else
    echo "未找到进程：$process_name"
  fi

else
  echo "cims 用户不存在"
fi
source "$esadmin_home/.bashrc"
echo "$RUN_STATE"