# Git--从入门到精通（高见龙）

### 1.概念辨析

1. 工作目录（working directory）--git add-->暂存区域（staging area）--git commit-->存储库（repository）
2. 每次的commit编号是根据**文本内容**，使用SHA-1(secure hash algorithm 1)计算得到的重复率非常低的文本，作为每次commit的识别标记码 
3. 有些版本控制系统会备份每次commit之间改动的历史记录：如增加2行，删除了3行。然后通过这些记录，还原文件。但是**Git并不是做差异备份**，即使只改动一个字，因为计算出来的SHA-1值不同，Git也会为它做出一个新的Blob对象。可以发现，相比于空间的浪费，Git更在意快速高效的控制版本。
4. Git合适自动触发垃圾回收
   1. 当.git/objects目录对象或者打包过的packfile数量过多时
   2. 当执行git push把内容推送到远端服务器时
5. HEAD是一个指针，指向某一个分支，通常可以把它看做“当前所在分支”。

### 2. 指令解读

#### 2.1 提交

| 指令                             | 注释                                                         |
| -------------------------------- | ------------------------------------------------------------ |
| git add . 与 git add --all的区别 | 前者只对当前目录生效，后者对整个项目生效                     |
| git commit -a -m "update file"   | 添加工作目录的modified文件到暂存区、存储区，但是不包括Untracked files(即新加入的new file) |

#### 2.2 查找记录

| 指令                                                         | 注释                                            |
| ------------------------------------------------------------ | ----------------------------------------------- |
| git log --oneline --author="tom\\|jerry"                     | 查找作者为tom或者jerry的提交记录，\\|为\|的转义 |
| git log --oneline --grep=“test”                              | 管道符查找记录                                  |
| git log --oneline --since="9am" --until="12am" --after="2020-01" | 按照时间查找记录                                |
| git log test.txt                                             | 查看test.txt的提交记录                          |
| git log -p test.txt                                          | 查看test.txt的提交记录中具体的改动              |

#### 2.3 变更文件

| 指令                       | 注释                                           |
| -------------------------- | ---------------------------------------------- |
| git rm test.txt            | 等价于：1）rm test.txt, 2)git add test.txt     |
| git rm test.txt --cached   | 将test.txt移出git目录，变为untracked files     |
| git mv test.txt test2.text | 等价于：1)mv test.txt test2.text，2）git add . |

#### 2.4 修改提交

##### 2.4.1 初级

| 指令                                              | 注释                                |
| ------------------------------------------------- | ----------------------------------- |
| git commit --amend -m "welcome"                   | 修改最近一次提交记录中的message     |
| 1)git add new.txt，2)git commit --amend --no-edit | 把新文件new.txt合并到最近一次commit |

##### 2.4.2 reset模式

reset通常直译为"重置"，但是在Git中将reset认为是**goto**的意思更好理解。

reset的不同模式代表着：在版本切换（到更旧或者更新的版本）后，如何处理暂存区和工作目录的修改（注意，尽管版本不同，各个版本之间**共享**着同一个暂存区和工作目录！）

1. --mixed: 默认参数，该模式会把暂存区的文件删除，但不影响工作目录的文件
2. --soft：对暂存区、工作目录文件均不会删除，看起来仅仅只有HEAD移动了而已
3. --hard：对暂存区、工作目录文件均会删除

| 指令                                       | 注释     |
| ------------------------------------------ | -------- |
| git reset --mixed/soft/hard 版本号\|HEAD~2 | 版本切换 |

##### 2.4.3 查看commit版本切换的记录

| 指令                             | 注释                     |
| -------------------------------- | ------------------------ |
| git reflog/ git log -g --oneline | 查看commit版本切换的记录 |

尽管看起来--hard之后仍然可以回到之前的版本，但是注意，在工作目录做出的新的修改（尚未commit ）就找不回来了。所以还是慎用-hard。

#### 2.5 提交时忽略文件

| 指令                | 注释                                                         |
| ------------------- | ------------------------------------------------------------ |
|                     | 将不想提交的文件配置到.gitignore<br />但是如果该文件在.gitignore生效前已经提交，则忽略无效<br />将该文件先移出git目录即可（git rm test.txt --cached） |
| git add -f test.txt | 强制提交被忽略为文件                                         |
| git clean -fX       | 清除被忽略的文件                                             |

#### 2.6 查看代码具体的行作者

| 指令                                           | 注释                                    |
| ---------------------------------------------- | --------------------------------------- |
| git blame test.txt/ git blame -L 5,10 test.txt | 查看每行代码的作者，-L 5,10：5-10行代码 |

#### 2.7 checkout文件

##### 2.8 误删除文件恢复

当使用git checkout指令时，git会切换到指定的分支，但是如果后边接的是文件名或者路径，git不会切换分支，而是把文件从.git目录中复制一份到当前的工作目录。更精确地说，这个命令会把暂存区的内容或者文件拿来覆盖工作目录中的内容或者文件。

| 指令                         | 注释                                                       |
| ---------------------------- | ---------------------------------------------------------- |
| git checkout test.txt        | 恢复被误删除的test.txt文件                                 |
| git checkout HEAD~2 test.txt | 距离现在两个版本以上的test.txt文件会覆盖当前目录的test.txt |

##### 2.8 差别比较

| 指令              | 注释                                                         |
| ----------------- | ------------------------------------------------------------ |
| git diff test.txt | 比较**工作目录**与**暂存区**的内容差别；当尚未add任何改动到暂存区时，暂存区为上一次commit时暂存区的内容（即，不要认为comiit之后暂存区就被清空了！） |

#### 2.9 分支管理

分支实际上只是一个标签，而并非复制。

切换分支时：

1. 暂存区会被新分支指向的commit内容覆盖
2. 工作区
   1. 当前分支与新分支指向相同commit：无论工作区有无改动，保留工作区
   2. 当前分支与新分支指向不同commit
      1. 工作区无改动：工作区被新分支指向的commit覆盖
      2. 工作区有改动：无法切换分支！

![创建新分支](E:/GitWorkspace/ReadingNotes/git_rookie_to_master/images/git_branch.png)

创建新分支↑

![创建新分支](E:/GitWorkspace/ReadingNotes/git_rookie_to_master/images/git_checkout.png)

切换到新分支↑

![创建新分支](E:/GitWorkspace/ReadingNotes/git_rookie_to_master/images/git_commit_branch.png)

新分支上进行commit↑

| 指令                    | 注释                    |
| ----------------------- | ----------------------- |
| git branch              | 查看当前有哪些分支      |
| git branch cat          | 创建一个叫cat的分支     |
| git branch -m cat tiger | 修改cat分支名字为tiger  |
| git branch -d cat       | 删除cat分支             |
| git branch -D cat       |                         |
| git checkout cat        | 切换到cat分支           |
| git checkout -b dog     | 创建并同时切换到dog分支 |



