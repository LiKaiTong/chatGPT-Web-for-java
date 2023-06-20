# ChatGPT-Web for java，基于Spring框架和本地MySQL数据库的个人ChatGPT网页版

## 特性

> 配置简单
>
> 聊天记录本地保存
>
> 多会话管理
>
> 流式对话内容实时显示
>
> 代码高亮



## 效果演示

![demonstrateGIF](https://github.com/LiKaiTong/chatgpt-web/assets/38353088/3b389c7f-a182-4b88-8bee-646fd9468fbb)

## 使用说明

### 数据库配置
create schema chatgptweb collate utf8mb4_0900_ai_ci;

create table gpt_dialogue
(
	id int auto_increment
		primary key,
	dialogue_id varchar(36) not null,
	dialogue_name varchar(20) not null comment '默认对话',
	constraint gpt_dialogue_dialogue_id_uindex
		unique (dialogue_id)
);

create table gpt_message
(
	id int auto_increment
		primary key,
	dialogue_id varchar(36) not null,
	role varchar(20) not null,
	content varchar(4096) not null
);

### Spring配置文件
![image](https://github.com/LiKaiTong/chatgpt-web/assets/38353088/cd13ba9d-7e66-48ad-a921-5171c5756d06)
设置本地MySQL数据库的账号，密码和地址

![image](https://github.com/LiKaiTong/chatgpt-web/assets/38353088/ccf7f9fe-7533-4d09-a608-a44d98811437)
配置GPT相关设置，包括API Key，如果使用代理，则配置相应端口

### 运行并访问本地8080端口即可使用
