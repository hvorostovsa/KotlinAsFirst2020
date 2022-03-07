1) Делаю форк KotlinAsFirst2020

2) Создаю клон проекта у себя

$ git clone https://github.com/hvorostovsa/KotlinAsFirst2020
$ cd KotlinAsFirst2020

3) Добавляю upstream-my

$ git remote add upstream-my https://github.com/hvorostovsa/KotlinAsFirst2021
$ git fetch upstream-my

4) Беру  хеш последнего коммита до моих решений и делаю rebase в master

 $ git rebase --onto master 1137b420cc95fa6894edad69b31e2da1bb985d1d upstream-my/master

5) Столкнулся с конфликтом 
 
error: could not apply 4da04f4... lesson 3
Resolve all conflicts manually, mark them as resolved with
"git add/rm <conflicted_files>", then run "git rebase --continue".
You can instead skip this commit: run "git rebase --skip".
To abort and get back to the state before "git rebase", run "git rebase --abort".
Could not apply 4da04f4... lesson 3
Auto-merging tutorial/chapter04.adoc
CONFLICT (content): Merge conflict in tutorial/chapter04.adoc
Auto-merging src/lesson4/task1/List.kt
Auto-merging src/lesson3/task1/Loop.kt

6) Решаю конфликт и заканчиваю rebase

$ git add tutorial/chapter04.adoc
$ git add src/lesson4/task1/List.kt
$ git add src/lesson3/task1/Loop.kt
$ git rebase --continue

7) Создаю ветку backport 

$ git branch backport

8) Загружаю в backport коммиты

$ git checkout master
$ git merge backport

9) Добааляю upstream-theirs и мерджу свои решения с чужими. 
Конфликты решаю в свою пользу.

$ git remote add upstream-theirs https://github.com/FAbrickA/KotlinAsFirst2021
$ git fetch upstream-theirs
$ git merge -s ours upstream-theirs/master

10) Создаю файл remotes

$ git remote -v > remotes
$ git add remotes
$ git commit -m "Adding a remote file"

11) Создаю файл howto

$ touch howto.md
$ git add howto.md
$ git commit -m "Adding a howto file"

12) Отправляю все на гитхаб

$ git push
$ git checkout backport
$ git push --set-upstream origin backport