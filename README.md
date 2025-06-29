# README


## Github 提交代码
注意添加Deploy Key
1. 第一步 生成 SSH pair key
```git bash
 ssh-keygen -t ed25519 -C "bull.king.smile@gmail.com" -f ~/.ssh/github_deploy_key 
```
2. 第二步 添加 key 到SSH-Agent
```git bash
eval $(ssh-agent -s)
ssh-add ~/.ssh/github_deploy_key
```
3. 第三步 添加public key到github -> setting -> SSH(or repo->setting->DeployKey)
4. 第四步 检查key是否关联生效
```git bash
ssh -T git@github.com
```
> Hi BullKing-Smile/TXAI-public! You've successfully authenticated, but GitHub does not provide shell access.

5. 第五步 (非必须，如果出现 git@github.com: Permission denied (publickey))
```git bash
git config core.sshCommand "ssh -i ~/.ssh/github_deploy_key"
```


Passenger Token
```json
{
  "code": 1,
  "message": "success",
  "data": {
    "accessToken": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJjcmVhdGVfdGltZSI6IjE3NTExNzk5OTg5NzQiLCJwaG9uZSI6IjEzODAwMDA4ODg5IiwiaWRlbnRpdHkiOiIxIiwidG9rZW5UeXBlIjoiMSIsImV4cCI6MTc1MTI2NjM5OH0.MsNzLOyt84IPNyeRrPQg1WD2flNeYnuMPVsHgNoeb2g",
    "refreshToken": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJjcmVhdGVfdGltZSI6IjE3NTExNzk5OTg5NzkiLCJwaG9uZSI6IjEzODAwMDA4ODg5IiwiaWRlbnRpdHkiOiIxIiwidG9rZW5UeXBlIjoiMiIsImV4cCI6MTc1Mzc3MTk5OH0.ZDNM4EvfLW3ZiGCKVYt52E-SCPu831a4plPo0aggQXw"
  }
}
```

Driver Token
```json
{
  "code": 1,
  "message": "success",
  "data": {
    "accessToken": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJjcmVhdGVfdGltZSI6IjE3NTA5OTk5ODA1MzciLCJwaG9uZSI6IjEzODAwMDA4ODg4IiwiaWRlbnRpdHkiOiIyIiwidG9rZW5UeXBlIjoiMSIsImV4cCI6MTc1MTA4NjM4MH0.kyVooLpoh0iAnDy1RtLtxIRB0rotcIUrgxaXSDMR8_Y",
    "refreshToken": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJjcmVhdGVfdGltZSI6IjE3NTA5OTk5ODA1NDAiLCJwaG9uZSI6IjEzODAwMDA4ODg4IiwiaWRlbnRpdHkiOiIyIiwidG9rZW5UeXBlIjoiMiIsImV4cCI6MTc1MzU5MTk4MH0.6GLdv6qSOmSiWetUYpCPYnQy8aBmtqZxcD9xIQ1yNSM"
  }
}
```
