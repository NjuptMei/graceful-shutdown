#!/bin/bash

# 检查是否存在 cims 用户
if id "esadmin" >/dev/null 2>&1; then
  esadmin_home=$(eval echo "~esadmin")
  # 切换到 cims 用户
  if [ -f "$esadmin_home/.bashrc" ]; then
    # 检查是否存在 RUN_STATUS 环境变量
    if grep -q "RUN_STATUS=" "$esadmin_home/.bashrc"; then
      # 存在 RUN_STATE 环境变量，将其值修改为 "running"
	  current_run_status=$(grep "RUN_STATUS=" "$esadmin_home/.bashrc" | cut -d'=' -f2-)
	  if [[ "$current_run_status" != "running" ]]; then
        sed -i 's/RUN_STATUS=.*/RUN_STATUS=running/g' "$esadmin_home/.bashrc"
	  fi		
    else
      # 不存在 RUN_STATE 环境变量，新增环境变量设置
      echo "export RUN_STATUS=\"running\"" >> "$esadmin_home/.bashrc"
    fi
  else
    echo "无法找到 ~/.bashrc 文件"
    exit 1
  fi
  source "$esadmin_home/.bashrc"
  echo "app状态已修改，启动项目"
  
  # 找到当前目录下的所有jar文件
  jars=$(find . -name "*.jar" -type f)
  
  # 打印可选择的jar文件列表
  echo "可执行的jar文件："
  echo "$jars"
  echo
  
  # 读取用户输入的数字指令
  read -p "请输入要执行的jar文件序号（1，2，3...）： " choice
  # 检查选择的编号是否有效
  if [[ $choice =~ ^[0-9]+$ ]] && [ $choice -gt 0 ] && [ $choice -le $(echo "$jars" | wc -l) ]; then
      selected_jar=$(echo "$jars" | sed -n "${choice}p")
  
      # 检查选择的jar文件是否已经在运行
      if pgrep -f "$(basename "$selected_jar")" >/dev/null; then
          echo "程序已经在运行！"
      else
          echo "启动 $selected_jar ..."
          nohup java -jar "$selected_jar" &
      fi
  else
      echo "无效的选择！"
	  exit 1
  fi
else
  echo "cims 用户不存在"
fi
source "$esadmin_home/.bashrc"
echo "$RUN_STATE"