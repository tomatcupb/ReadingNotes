# Git--从入门到精通（高见龙）

### 1.概念辨析

1. 工作目录（working directory）--git add-->暂存区域（staging area）--git commit-->存储库（repository）

2. 每次的commit编号是根据**文本内容**，使用SHA-1(secure hash algorithm 1)计算得到的重复率非常低的文本，作为每次commit的识别标记码 

3. 有些版本控制系统会备份每次commit之间改动的历史记录：如增加2行，删除了3行。然后通过这些记录，还原文件。但是**Git并不是做差异备份**，即使只改动一个字，因为计算出来的SHA-1值不同，Git也会为它做出一个新的Blob对象。可以发现，相比于空间的浪费，Git更在意快速高效的控制版本。

4. Git合适自动触发垃圾回收
   1. 当.git/objects目录对象或者打包过的packfile数量过多时
   2. 当执行git push把内容推送到远端服务器时
   
5. HEAD是一个指针，指向某一个分支，通常可以把它看做“当前所在分支”。

   ![HEAD](E:\GitWorkspace\ReadingNotes\git_rookie_to_master\images\head.png)

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

牢记：分支实际上分支只是一个40个字节的**标签**，而**并非复制**。

![分支](E:\GitWorkspace\ReadingNotes\git_rookie_to_master\images\branch.png)

##### 2.9.1 分支的创建与切换

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

| 指令                        | 注释                                               |
| --------------------------- | -------------------------------------------------- |
| git branch                  | 查看当前有哪些分支                                 |
| git branch cat              | 创建一个叫cat的分支                                |
| git branch -m cat tiger     | 修改cat分支名字为tiger                             |
| git branch -d cat           | 删除cat分支                                        |
| git branch -D cat           |                                                    |
| git checkout cat            | 切换到cat分支                                      |
| git checkout -b dog         | 创建并同时切换到dog分支                            |
| git branch cat  65fec4      | 根据历史commit 65fec4创建一个新分支cat             |
| git checkout -b cat  65fec4 | 根据历史commit 65fec4创建一个新分支cat，并切换分支 |

##### 2.9.2 merge分支合并

| 指令                             | 注释                                                     |
| -------------------------------- | -------------------------------------------------------- |
| git merge dev1/ git merge s3gb4f | 合并目标分支到当前分支，合并成功后当前分支移动一个commit |
| git branch new_dev1 b2e3fd       | 恢复已经删除的分支dev1，b2e3fd是dev1分支最后指向的commit |
| git merge --abort                | 出现冲突时，放弃合并分支                                 |

- 快转模式合并（Fast Forward）：当两个分支是**父子关系**，直接移动标签即可

git merge cat(当前在master分支)↓

![快转模式合并](E:\GitWorkspace\ReadingNotes\git_rookie_to_master\images\git_branch_merge_FF.png)

- 非快转模式合并（Fast Forward）：当两个分支是**兄弟关系**，会产生一个新的commit，再移动标签

git merge dog(当前在cat分支)↓

![非快转模式合并](E:\GitWorkspace\ReadingNotes\git_rookie_to_master\images\git_branch_merge_no_FF.png)

- 合并冲突解决

FF模式的合并不存在冲突，只有标签移动而已。

非FF模式合并时，出现冲突的话需要先手动解决冲突，然后在commit即可。

##### 2.9.3 rebase分支合并

| 指令           | 注释                    |
| -------------- | ----------------------- |
| git rebase dog | 以dog为基础版本进行合并 |

类似于嫁接，原先cat分支上两次commit还会存在知道Git垃圾回收清理之，总的来看会减少分支的数量。

使用rebase的时机：对于那些没有push出去的但是感觉有点琐碎混乱的commit，可以先使用rebase来整理分支，然后再推出去。rebase相当于改动了历史记录，而改动已经push出去的历史记录可能给他人带来困扰。因此，如果没有必要，尽量不要使用rebase。

![rebase分支合并](E:\GitWorkspace\ReadingNotes\git_rookie_to_master\images\git_branch_rebase.png)

#### 2.10 修改历史记录（高阶rebase用法）

#### 2.11 标签（tag）

标签与分支都只是一种指向commit对象的指示标，二者的删改均不会影响到被指向的那个对象。二者的区别在于分支会随着Commit而移动，而标签不会。

#### 2.12 远程协作

##### 2.12.1 push

```
git remote add origin https://github.com/tomatcupb/test_remote.git
```

这一步是设置远端节点

1. git remote指令主要是进行与远端相关的操作
2. add 指令是要加入一个远端的节点
3. 这里的**origin**是一个代名词（惯例，可以改其他的名字），指的是那串**GitHub服务器的位置**。

```
git push -u origin master
```

在设置好远端节点后，接下来就是把内容推上去

1. 把master分支的内容推向origin的位置
2. 在origin远端服务器上，如果master不存在，就创建一个名为master的分支
3. 如果远端master分支存在，则移动远端master分支的位置，使它指向当前最新的进度
4. -u参数为设置upstream，第一次设置后之后就可以不用再加上了

##### 2.12.2 pull

![原分支](E:\GitWorkspace\ReadingNotes\git_rookie_to_master\images\git_pull.png)

```
git pull = git fetch + git merge
```

![fetch+merge](E:\GitWorkspace\ReadingNotes\git_rookie_to_master\images\git_fetch.png)

pull指令其实就是将线上内容抓下来（Fetch），再更新进度（Merge）。远端的origin/master也是master的一个分支，因此fetch到本地后，之需要通过Fast Forward模式进行合并即可。

clone与pull的区别：

clone一般在第一次下载项目时使用（该路径没有被git init过），而后将使用pull/fetch获取更新