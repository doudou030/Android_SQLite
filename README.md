# Android_SQLite
>在Android_studio使用SQLite並且以Listview顯示出來

>文字說明與程式碼皆取自於Android 初學特訓班，第九版

## 認識SQLite資料庫
- SQLite是嵌入式的資料庫(embedded SQL database)
	- 適合用在資料固定且量不多

### 建立SQLite資料庫
```java=
SQLiteDatabase 物件 = openOrCreateDatabase(檔名, 權限, null);
```
- `檔名` : 資料庫的名稱，附檔名預設為`.db`
- `權限` : 資料庫的存取權限
	- `MODE_PRIVATE` : 只有本應用程式有讀寫的存取權
	- `MODE_WORLD_READABLE` : 所有應用程式都可以讀
	- `MODE_WORLD_WRITEABLE` : 所有應用程式都可以寫

### 刪除SQLite資料庫
```java=
delDatabase("db1.db"); 
```
## SQLite Database類別

|     程式     |       用途       |
|:------------:|:----------------:|
| `execSQL()`  |   執行SQL命令    |
| `rawQuery()` |   查詢所有資料   |
|  `insert()`  |     資料新增     |
|  `delete()`  |     刪除資料     |
|  `update()`  |     修改資料     |
|  `query()`   | 傳回指定欄位資料 |
|  `close()`   |    關閉資料庫    |
>要用的話就要db.xx
### 使用`execSQL()`方法執行SQL命令
- `db.execSQL(str)` str : 要用SQL語法 
-   ```java=
    "CREATE TABLE table01(_id INTEGER PRIMARY KEY,num INTEGER ,data TEXT)";
    ```
    >建立(_id、num、data)，_id為自動編號，num為整數，TEXT為文字欄位
-   ```java=
    "INSERT INTO table01 (num,data) values (1,'資料項目1')";
    ```
    >在table01新增一筆資料，_id是自動的，因次只需要輸入num跟data
-   ```java=
    "UPDATE table01 SET num=12, data='資料更新' WHERE _id=1";
    ```
    >更新table01資料表，編號為`_id=1`
-   ```java=
    "DELETE FROM table01 WHERE _id=1";
    ```
    >移除table01資料表，編號為`_id=1`
-   ```java=
    "DROP TABLE table01";
    ```
    >刪除table01資料表

在`onDestroy()`裡需要先刪除原本的資料表`DROP TABLE table01`，然後在關閉資料庫`db.close`，才能在每次開啟資料庫時才是空的

### 資料查詢rawQuery()、query()
- `rawQuery()`會以`Cursor`類別回傳資料查詢的結果
	-   ```java=
		//查詢所有資料
		Cursor cursor = db.rawQuery("SELECT * FROM table01", null);
		```
	-   ```java=
		//查詢_id=1的資料
		Cursor cursor = db.rawQuery("SELECT * FROM table01 WHERE _id=1", null);
		```
	-   ```java=
		Cursor cursor = db.rawQuery("select * from table01 where _id >= ? and _id<=?", new String[]{"2","3"});
		//查詢_id>=2且_ud<=3的資料，也相當於下面這個寫法
		Cursor cursor = db.rawQuery("select * from table01 where _id >= 2 and _id<= 3", null);
		```
- `query()`則是把`rawQuery()`拆開成不同參數，一樣是回傳`Cursor`類別
	-	```java=
		Cursor cursor = query(String table, String[] columns, String selection,
				String[] selectionArg, Strint groundBy, String having,
				String orderBy, String limit);
		//table:資料表名稱
		//columns:代表指定資料的欄位，null代表全部
		//selection:指定的查詢條件，null代表全部
		//selectionArg:定義SQL WHERE中的?參數
		//groundBy:設定群組，null代表不指定
		//having:設定群組條件，null代表不指定
		//orderBy:設定排序，null代表不指定
		//limit:取得的資料筆數，null代表不指定
		```
- `query()`的範例
	-	```java=
		//查詢table01中的所有資料，並傳回num和data兩個欄位的資料
		db.query("table01", new String[] {"num","data"}, null, null, null, null, null, null);
		```
	-	```java=
		//查詢table01中_id=1的資料，並傳回num和data兩個欄位的資料
		db.query("table01", new String[] {"num","data"}, "_id=1", null, null, null, null, null);
		```
	-	```java=
		//查詢table01中依_id由大到小的前3筆資料，並傳回num和data兩欄位的資料
		db.query("table01", null, null, null, null, null, "_id desc", "3");
		```

**cursor就是從資料庫中取出的資料，以下為cursor常用的用法**

|      方法      |              用途               |
|:--------------:|:-------------------------------:|
|      move      |     將cursor移到指定的位置      |
| moveToPosition |     將cursor移到指定的位置      |
|  moveToFirst   |       將cursor移到第一筆       |
| moveToPrevious |       將cursor移到上一筆       |
|   moveToNext   |       將cursor移到下一筆       |
|   moveToLast   |      將cursor移到最後一筆      |
| isBeforeFirst  |     傳回cursor是否在第一筆      |
|  isAfterLast   |    傳回cursor是否在最後一筆     |
|    isClosed    |       傳回cursor是否關閉       |
|    isFirst     |   傳回cursor是否是第一筆資料    |
|     isNull     |      傳回cursor是否為null     |
|    getCount    |    傳回cursor共有多少筆資料     |
| getPosition()  |  傳回cursor目前所在位置的索引   |
|     getInt     | 取得指定索引的Integer類別的資料 |
|   getString    | 取得指定索引的String類別的資料  |


### 資料新增insert()
```java=
ContentValues cv = new ContentValues(); //建立ContentValues物件
cv.put("data", "西瓜");
cv.put("num", "120");
db.insert("table01", null, cv);
```
上下效果一樣，上面是用`insert()`，下面是用`execSQL()`
```java=
String str = "INSERT INTO table01 (data,num) values ('西瓜',120)";
db.execSQL(str);
```
### 資料刪除delete()
```java=
int id = 1;
db.delete("table01","_id=" + id, null)
```
上下效果一樣，上面是用`delete()`，下面是用`execSQL()`
```java=
String str = "DELETE FROM table01 WHERE _id=1";
db.execSQL(str);
```
### 資料更新update()
```java=
ContentValues cv = new ContentValues(); //建立ContentValues物件
cv.put("data", "南瓜");
cv.put("num", "135");
db.update("table01", cv, "_id=1", null);
```
上下效果一樣，上面是用`update()`，下面是用`execSQL()`
```java=
String str = "UPDATE table01 SET num=135, data='南瓜' WHERE _id=1";
db.execSQL(str);
```

**SQL語法中，字串型別須加上引號，而數值則不用加上引號**
`'南瓜'` `135`

## 以ListView顯示SQLite資料
### SimpleCursorAdapter
>SimpleCursorAdapter是用來當作顯示介面元件和Cursor資料的橋樑
>像是可以用在ListView和Spinner
- SimpleCursorAdapter建構式
```java=
SimpleCursorAdapter(Context context, int layout, Cursor cursor,
		String[] from, int[] to, int flags)
//context:目前的主程式
//layout:顯示的layout
//cursor:要顯示的資料
//from:顯示的欄位
//to:對應的顯示元件
//flags:效能問題，輸入0就好
```
- SimpleCursorAdapter範例
	- 將table01資料表中所有資料顯示在listview01這個元件上
	- 版面配置使用內建的`android.R.layout.simple_list_item_2`
	- 資料欄位為num、data
	- 版面配置中對應顯示元件為`android.R.id.text1`和`android.R.id.text2`
```java=
Cursor cursor = db.rawQuery("SELECT * FROM table01", null);
SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
android.R.layout.simple_list_item_2,//包含兩個資料向的內建版面
cursor,
new String[] {"num","data"},
new int[]{android.R.id.text1, android.R.id.text2},0);
listview01.setAdapter(adapter); 
```
### 將SQLite資料顯示在ListView上
- ListView顯示全部資料
- 輸入ID並按下**查詢**可以查詢對應的_id
- 按下**查詢全部**則會顯示全部
- 若資料不存在則顯示查無此資料

**合併資料欄位**

`_id || '.' || name pname`就會變成((_id).(name))
不過在下面Adapter呼叫的時候就要用`pname`
:::

## DEMO
![image](https://github.com/doudou030/Android_SQLite/assets/95849271/5a1dbdba-6c61-4f65-8195-01d49c77dc5f)
