<font size=4>**一、简介**</font>

&emsp;OrmLite是一个数据库框架，这个可以让我们快速实现数据库操作，避免频繁手写sql，提高我们的开发效率，减少出错的机率。
&emsp;首先可以去它的官网看看[www.ormlite.com](www.ormlite.com)，它的英文全称是Object Relational Mapping，意思是**对象关系映射**；如果接触过Java EE开发的，一定知道Java Web开发就有一个类似的数据库映射框架——Hibernate。简单来说，就是我们定义一个实体类，利用这个框架，它可以帮我们吧这个实体映射到我们的数据库中，在Android中是SQLite，数据中的字段就是我们定义实体的成员变量。
**优点**
1）轻量级
2）使用简单，易上手
3）封装完善
4）文档全面
**缺点**
1）基于反射，效率较低
2）缺少中文翻译文档

<font size=4>**二、运用**</font>

**1. 集成**
&emsp;首先到Ormlite官网下载Android的架包：[http://ormlite.com/releases/](http://ormlite.com/releases/)。
![这里写图片描述](http://img.blog.csdn.net/20160719142830136)
&emsp;可以看到，目前最新版本为4.49，对于Android的架包为: ormlite-android-4.48.jar 和 ormlite-core-4.48.jar。把两个jar拷贝到libs下，一般Gradle里面都会包含编译libs: 
```
compile fileTree(dir: 'libs', include: ['*.jar'])
```
![这里写图片描述](http://img.blog.csdn.net/20160719143022501)
&emsp;编译后就可以看到以上架包结构。

**2.配置Bean类**

&emsp;数据库肯定离不开Bean类，先来看看我建的一个User类：
```
@DatabaseTable(tableName = "user")
public class User {

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(columnName = "name")
    private String name;
    @DatabaseField(canBeNull = false, columnName = "desc")
    private String desc;

    /*关键啊，一定要加, 为每个class添加一个无参的构造器，并且构造器在包内是可见的*/
    public User() {}

    public User(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}
```
&emsp;首先在User类上添加@DatabaseTable(tableName = "user")，标明这是数据库中的一张表，标明为user；然后分别在属性上添加@DatabaseField(columnName = "name") ，columnName的值为该字段在数据中的列名@DatabaseField(generatedId = true) ，generatedId 表示id为主键且自动生成，canBeNull表示该属性的内容不能为空，下面来介绍更多常用的属性设置：
|成员名|数据类型 | 描述|
| :--:| :--:| -- |
|generatedId|Boolean|字段是否自动增加。默认为false。类中的一个成员变量设置了这个值，它告诉数据库每添加一条新记录都自动增加id。当一个有generatedid的对象被创建时使用Dao.create()方法，数据库将为记录生成一个id，它会被返回并且被create方法设置进对象。 |
|columnName|String |数据库的列名。如果你没有设置这个成员名，会用标准的形式代替它。|
|canBeNull |Boolean|字段是否能被分配null值。默认是true。如果你设置成false，那么你每次在数据库中插入数据是都必须为这个字段提供值。  |
|dataType| |字段的数据类型。通常情况下，数据类型是从java类的成员变量获取的，并不需要进行特殊指出。它相当于是SQL的数据类型。|
|defaultValue|String |当我们在表中创建新的记录时的一个字段的默认值。默认情况下是没有这个值的。|
|width|Integer|字段的宽度，主要用于字符串字段。默认是0，意味着采用默认的数据类型和具体的数据库的默认情况。|
|id|Boolean|这个字段是否是id，默认是false。在一个class中只有一个成变量可以有这个值。|
|columnName|String |数据库的列名。如果你没有设置这个成员名，会用标准的形式代替它。|

**3.封装Dao类**
&emsp;在普通Sqlite使用过程中，我们都会自己写个Helper类来继承SQLiteOpenHelper，然后里面封装一些方法来操作数据库，OrmLite也一样，但我们需要继承的是OrmLiteSqliteOpenHelper：
```
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String TABLE_NAME = "demo.db";

    private static Context mApplicationContext;

    private static DatabaseHelper instance;

    private Map<String, Dao> daoMaps = new HashMap<>();

    private DatabaseHelper(Context context) {
        super(context, TABLE_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, User.class);
            TableUtils.createTable(connectionSource, Article.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, User.class, true);
            TableUtils.dropTable(connectionSource, Article.class, true);
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void initOrmLite(Context context) {
        mApplicationContext = context;
        getInstance();
    }

    /**
     * 单例获取该Helper
     */
    public static DatabaseHelper getInstance() {
        if (instance == null) {
            synInit(mApplicationContext);
        }
        return instance;
    }

    private synchronized static void synInit(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context);
        }
    }

    /**
     * 获得Dao
     *
     * @return
     * @throws SQLException
     */
    public synchronized Dao getDao(Class clazz) throws SQLException {
        Dao dao;
        String className = clazz.getSimpleName();

        if (daoMaps.containsKey(className)) {
            dao = daoMaps.get(className);
        } else {
            dao = super.getDao(clazz);
            daoMaps.put(className, dao);
        }
        return dao;
    }

    /**
     * 释放资源
     */
    @Override
    public void close() {
        super.close();
        for (String key : daoMaps.keySet()) {
            Dao dao = daoMaps.get(key);
            dao = null;
        }
    }

}
```
&emsp;这里需要实现两个方法：
创建表，我们直接使用ormlite提供的TableUtils.createTable(connectionSource, User.class);进行创建。
```
onCreate(SQLiteDatabase database,ConnectionSource connectionSource)
```
更新表：使用ormlite提供的TableUtils.dropTable(connectionSource, User.class, true);进行删除操作~
```
onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion)
```
&emsp;我这里封装的是单例，所以一般最好在Application的OnCreate方法初始化：
```
DatabaseHelper.initOrmLite(this);
```
&emsp;我看了很多人的写法，都喜欢在Helper里面写各种方法，获取各种Dao来操作数据库，要是应用数据复杂些，就会有很多很多的方法，复杂不容易管理，就像一个Activity写了几千行代码，让我是没兴趣去往下看方法，找方法也麻烦。所以我再这个Helper里面只写了个简单的getDao方法，通过传入是Bean类找到它对应的Dao类，然后只操作对应的Dao类，这样就做出了分离；而且可以发现我使用了一个Map将Dao存起来，做了个缓存。下面来看看我封装的Dao类代码：
```
public class UserDao {

    public static UserDao mUserDaoInstance;

    private Dao<User, Integer> mUserDao;

    public UserDao() {
        try {
            mUserDao = DatabaseHelper.getInstance().getDao(User.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static UserDao getInstance() {
        if (mUserDaoInstance == null) {
            mUserDaoInstance = new UserDao();
        }
        return mUserDaoInstance;
    }

    /**
     * 单条插入数据
     */
    public void insertUser(User user) {
        try {
            mUserDao.create(user);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 多条插入数据
     */
    public void insertUsers(List<User> users) {
        try {
            mUserDao.create(users);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询所有数据
     */
    public List<User> queryAllUser() {
        List<User> users = new ArrayList<>();
        try {
            users = mUserDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    /**
     * 通过id查询数据
     */
    public User queryUserById(int id) {
        User user = null;
        try {
            user = mUserDao.queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    /**
     * 删除该id的数据
     */
    public void deleteUserById(int id) {
        try {
            mUserDao.deleteById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除这些id的数据
     */
    public void deleteUserByIds(List<Integer> ids) {
        try {
            mUserDao.deleteIds(ids);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除所有
     */
    public void deleteAllUser() {
        try {
            mUserDao.deleteBuilder().delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新当前实体类数据
     */
    public void updateUser(User user) {
        try {
            mUserDao.update(user);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新当前数据的id
     */
    public void updateUserById(User user, int id) {
        try {
            mUserDao.updateId(user, id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 自定义查询
     */
    public List<User> queryBy() throws SQLException {
        QueryBuilder<User, Integer> queryBuilder = mUserDao
                .queryBuilder();
        Where<User, Integer> where = queryBuilder.where();
        where.eq("id", 1);
        where.and();
        where.eq("name", "xxx");

        //或者
        mUserDao.queryBuilder().
                where().
                eq("id", 1).and().
                eq("name", "xxx");
        return queryBuilder.query();
    }
}
```
&emsp;我在这里也写成了单例模式，因为考虑到不止一个地方使用UserDao，使用代码如下：
```
User user = new User("jianglei", "金陵小霸王");
UserDao.getInstance().insertUser(user);
```
&emsp;其他的数据库操作同理，这样在维护一个数据库表的一些操作就比较简单了，在一个类里面维护，想找哪个数据库表的操作，就去对应类的Dao里面去找就行了。

&emsp;看完OrmLite，大家可以再看看更简单，效率更高的GreenDao框架，我也整理好了，大家可以对比下：http://blog.csdn.net/qq_19711823/article/details/51837032

&emsp;下面就是要讲比较枯燥的源码分析，没有兴趣的童鞋可以止步了。

<font size=4>**三、源码分析**</font>

**1.创建表**
&emsp;我们先来看看创建表的方法：
```
@Override
public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
     try {
         TableUtils.createTable(connectionSource, User.class);
     } catch (SQLException e) {
         e.printStackTrace();
     }
}
```
&emsp;大家可以跟踪源码，可以发现，最主要的是doCreateTable中的两句话：
```
addCreateTableStatements(databaseType, tableInfo, statements, queriesAfter, ifNotExists);

int stmtC = doStatements(connection, "create", statements, false, 
          databaseType.isCreateTableReturnsNegative(), databaseType.isCreateTableReturnsZero());
```
&emsp;先来看看addCreateTableStatements实现：
```
private static <T, ID> void addCreateTableStatements(DatabaseType databaseType, TableInfo<T, ID> tableInfo, List<String> statements, List<String> queriesAfter, boolean ifNotExists) throws SQLException {
        StringBuilder sb = new StringBuilder(256);
        sb.append("CREATE TABLE ");
        if(ifNotExists && databaseType.isCreateIfNotExistsSupported()) {
            sb.append("IF NOT EXISTS ");
        }

        databaseType.appendEscapedEntityName(sb, tableInfo.getTableName()); // 添加表名
        sb.append(" (");
        ArrayList additionalArgs = new ArrayList();
        ArrayList statementsBefore = new ArrayList();
        ArrayList statementsAfter = new ArrayList();
        boolean first = true;
        FieldType[] i$ = tableInfo.getFieldTypes(); // 获取表的属性
        int arg = i$.length;

        for(int i$1 = 0; i$1 < arg; ++i$1) {
            FieldType fieldType = i$[i$1];
            if(!fieldType.isForeignCollection()) {
                if(first) {
                    first = false;
                } else {
                    sb.append(", "); // 第一个参数之前不需要“,”
                }

                String columnDefinition = fieldType.getColumnDefinition();
                if(columnDefinition == null) {
                    databaseType.appendColumnArg(tableInfo.getTableName(), sb, fieldType, additionalArgs, statementsBefore, statementsAfter, queriesAfter);
                } else {
                    databaseType.appendEscapedEntityName(sb, fieldType.getColumnName());
                    sb.append(' ').append(columnDefinition).append(' '); // 添加列名
                }
            }
        }

        databaseType.addPrimaryKeySql(tableInfo.getFieldTypes(), additionalArgs, statementsBefore, statementsAfter, queriesAfter);
        databaseType.addUniqueComboSql(tableInfo.getFieldTypes(), additionalArgs, statementsBefore, statementsAfter, queriesAfter);
        Iterator var16 = additionalArgs.iterator();

        while(var16.hasNext()) {
            String var15 = (String)var16.next();
            sb.append(", ").append(var15);
        }

        sb.append(") "); // 拼装结束
        databaseType.appendCreateTableSuffix(sb);
        statements.addAll(statementsBefore);
        statements.add(sb.toString());
        statements.addAll(statementsAfter);
        addCreateIndexStatements(databaseType, tableInfo, statements, ifNotExists, false);
        addCreateIndexStatements(databaseType, tableInfo, statements, ifNotExists, true);
    }
```
&emsp;到这里，大家终于看到了一些熟悉的字眼，那些就是数据库最初的sql语句，这个函数主要是负责去生成我们要执行的sql语句。然后就是doStatements执行sql语句：
```
private static int doStatements(DatabaseConnection connection, String label, Collection<String> statements, boolean ignoreErrors, boolean returnsNegative, boolean expectingZero) throws SQLException {
    int stmtC = 0;
    for(Iterator i$ = statements.iterator(); i$.hasNext(); ++stmtC) {
        String statement = (String)i$.next();
        int rowC = 0;
        CompiledStatement compiledStmt = null;

        compiledStmt = connection.compileStatement(statement, StatementType.EXECUTE, noFieldTypes, -1);
        rowC = compiledStmt.runExecute();
        logger.info("executed {} table statement changed {} rows: {}", label,                          
                     Integer.valueOf(rowC), statement); 
        }

       ...
    return stmtC;
}
```

