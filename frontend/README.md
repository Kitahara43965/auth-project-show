
##オートリロードについて
$ ./gradlew bootRun（コンパイル）<br>
他のターミナルウィンドウで
$ ./gradlew classes -t
Ctrl + F5（強制リロード）<br>
シークレットモード<br>
URL再入力（今やってるやつ）<br>
<br>
テストは$ ./gradlew clean test）コンパイル<br>
特定のテストは$ ./gradlew clean test --tests ProgramTest<br>

##DB初期化について<br>
(権限不要時) scripts/reset-dev-databases.sh<br>
(権限必要時) chmod +x scripts/reset-dev-databases.sh<br>

##dbへの切り替え<br>
\c database<br>
->databaseデータベースへの切り替え<br>
例：\c my_auth<br>
->my_authデータベースへの切り替え<br>

##Auditlog未分類振り分けコマンド<br>
./gradlew bootRun --args="--audit-log-recovery"<br>


<br>
##flyway確認<br>
SELECT version, description, success<br>
FROM flyway_schema_history<br>
ORDER BY installed_rank;<br>
<br>
##flywayエラー時の場合<br>
$./gradlew test --tests "com.example.myauthapp.support.context.MyAuthAppApplicationTests" --info<br>
最初の500行ぐらいまでで情報が取れる<br>

 ##DB確認について<br>
 $psql -U apple -d my_auth<br>
 $SELECT * FROM users;<br>
 ##DBテスト確認について<br>
  $psql -U apple -d my_auth<br>
 $SELECT * FROM users;<br>
<br>
 localhost/3000/api/meでログイン状態が確認できる<br>

 DB BeaverでのDBの確認<br>
※application.yamlによる<br>
 Connect By: Host<br>
 Host: localhost<br>
 Port: 5432<br>
 Database: my_auth<br>
 Show all database:チェック<br>
 Authentication: Username/password<br>
 Username: apple<br>
 Password: (なし)<br>
 <br>
 Connections/Databases/my_authを確認<br>
 public<br>
 タブで「Properties」ではなく「Data」に入る<br>
<br>
<br>ブロックチェック
my_auth_test=# SELECT
    blocked.pid AS blocked_pid,
    blocked.query AS blocked_query,
    blocking.pid AS blocking_pid,
    blocking.query AS blocking_query
FROM pg_stat_activity blocked
JOIN pg_stat_activity blocking
    ON blocking.pid = ANY(pg_blocking_pids(blocked.pid))
<br>
reports/をhtmlでチェック<br>
open build/reports/tests/test/index.html<br>

import org.assertj.core.api.Assertions;<br>
Assertions.setMaxStackTraceElementsDisplayed(200);<br>
でエラーを多く出力できる（この場合200行）<br>

//TODO PartitionOperationのStatus-で強制終了時のRunningを治せるようにしよう（audit側が完了してもエラーになることもあるかも）