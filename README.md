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