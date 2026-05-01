# Auction System Backend Schema 

## commit 格式
ref：https://wadehuanglearning.blogspot.com/2019/05/commit-commit-commit-why-what-commit.html

---

## 1. User

| Field | Type | Constraints | Note |
|------|------|------------|------|
| id | SERIAL | PK | 使用者ID |
| account | VARCHAR(50) | UNIQUE, NOT NULL | 帳號 |
| passwd | VARCHAR(255) | NOT NULL | 密碼（建議hash） |
| id_num | VARCHAR(20) | UNIQUE, NOT NULL | 身分證字號 |
| phone_num | VARCHAR(20) |  | 電話 |
| email | VARCHAR(100) | UNIQUE | 信箱 |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | 建立時間 |

---

## 2. Item

| Field | Type | Constraints | Note |
|------|------|------------|------|
| id | SERIAL | PK | 商品ID |
| name | VARCHAR(100) | NOT NULL | 商品名稱 |
| owner_id | INT | FK -> User.id | 賣家 |
| category_id | INT | FK -> Category.id | 分類 |
| starting_price | NUMERIC(10,2) | NOT NULL | 起標價 |
| current_price | NUMERIC(10,2) | DEFAULT 0 | 目前最高價 |
| start_time | TIMESTAMP | NOT NULL | 開始時間 |
| end_time | TIMESTAMP | NOT NULL | 結束時間 |
| status | VARCHAR(20) | CHECK (status IN ('DRAFT','SCHEDULED','ACTIVE','ENDED','CANCELLED')) | 拍賣狀態 |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | 建立時間 |
| updated_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | 更新時間（需由後端更新） |
| image_path | VARCHAR(255) |  | 圖片路徑 |

---

## 3. Category

| Field | Type | Constraints | Note |
|------|------|------------|------|
| id | SERIAL | PK | 分類ID |
| name | VARCHAR(50) | UNIQUE, NOT NULL | 分類名稱 |

---

## 4. Bid

| Field | Type | Constraints | Note |
|------|------|------------|------|
| id | SERIAL | PK | 出價ID |
| item_id | INT | FK -> Item.id | 商品 |
| bidder_id | INT | FK -> User.id | 出價者 |
| bid_price | NUMERIC(10,2) | NOT NULL | 出價金額 |
| bid_time | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | 出價時間 |

---

## 5. User_Message

| Field | Type | Constraints | Note |
|------|------|------------|------|
| id | SERIAL | PK | 留言ID |
| target_user_id | INT | FK -> User.id | 被留言者 |
| author_user_id | INT | FK -> User.id | 留言者 |
| content | TEXT | NOT NULL | 留言內容 |
| rating | SMALLINT | CHECK (rating BETWEEN 1 AND 5) | 評分 |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | 建立時間 |

---

## 6. Notification

| Field | Type | Constraints | Note |
|------|------|------------|------|
| id | SERIAL | PK | 通知ID |
| user_id | INT | FK -> User.id | 接收者 |
| type | VARCHAR(20) | CHECK (type IN ('WIN_AUCTION','OUTBID','SYSTEM')) | 通知類型 |
| title | VARCHAR(100) |  | 標題 |
| content | TEXT |  | 內容 |
| sent_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | 發送時間 |

---

## 7. Auction_Result

| Field | Type | Constraints | Note |
|------|------|------------|------|
| id | SERIAL | PK | 結標ID |
| item_id | INT | FK -> Item.id, UNIQUE | 商品（唯一） |
| winner_id | INT | FK -> User.id | 得標者 |
| final_price | NUMERIC(10,2) |  | 最終價格 |
| closed_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | 結標時間 |

---

# INDEX (Performance Optimization)

```sql
-- 商品分類查詢加速
CREATE INDEX idx_item_category ON item(category_id);

-- 商品狀態查詢加速
CREATE INDEX idx_item_status ON item(status);

-- 查某商品所有出價
CREATE INDEX idx_bid_item ON bid(item_id);

-- 出價排序（找最高價）
CREATE INDEX idx_bid_price ON bid(bid_price);

-- 進階（建議）
CREATE INDEX idx_item_status_time ON item(status, start_time);
CREATE INDEX idx_bid_item_price ON bid(item_id, bid_price);
```
---
# Auction System Backend Architecture


## 0. 系統流程定義（API 流程設計依據）

### 使用者流程

```
Register → Login
        ↓
Browse Category → View Item → Bid
        ↓
Auction End → System Close → Send Mail
```

---

## 1. Backend 分層架構

系統採用標準分層設計：

* controller → HTTP 請求入口
* service → 業務邏輯處理層
* mapper → 資料庫存取層（MyBatis）
* entity → 資料庫對應模型

此結構符合一般後端系統設計原則。

---

## 2. Controller 設計（依 Domain 劃分）

Controller 不依據資料表拆分，而依功能領域（Domain）設計。

---

### AuthController

* POST `/api/auth/register`
* POST `/api/auth/login`
* GET `/api/auth/me`

---

### CategoryController

* GET `/api/categories`

（分類功能為查詢導向）

---

### ItemController

#### 建立拍賣

* POST `/api/items`

```json
{
  "name": "",
  "categoryId": 1,
  "startingPrice": 100,
  "startTime": "",
  "endTime": "",
  "image": ""
}
```

#### 查詢商品列表

* GET `/api/items?categoryId=1`

條件：僅回傳 ACTIVE 狀態商品

#### 商品詳細

* GET `/api/items/{id}`

包含：

* 商品資訊
* 目前最高出價
* 賣家資訊

#### 使用者商品

* GET `/api/items/my`

---

### BidController

#### 出價

* POST `/api/bids`

```json
{
  "itemId": 1,
  "bidPrice": 200
}
```

#### 查詢商品出價紀錄

* GET `/api/items/{id}/bids`

#### 查詢最高出價

* GET `/api/items/{id}/highest-bid`

---

### UserMessageController

#### 新增留言

* POST `/api/users/{id}/messages`

#### 查詢留言

* GET `/api/users/{id}/messages`

---

### NotificationController

* GET `/api/notifications`

---

### AuctionController

此 Controller 處理系統層級行為，而非一般 CRUD。

#### 手動結標（管理或測試用途）

* POST `/api/auctions/{itemId}/close`

#### 查詢結標結果

* GET `/api/auctions/{itemId}/result`

---

## 3. Service 層設計

Service 負責主要業務邏輯。

---

### AuctionService

負責拍賣結標相關流程：

* closeAuction()
* findWinner()
* createAuctionResult()
* sendNotification()
* sendEmail()
* updateItemStatus()

---

### BidService

* placeBid()
* validateBid()
* updateCurrentPrice()

必要驗證條件：

* auction 是否 ACTIVE
* 時間是否有效
* 出價是否高於目前最高價

---

### ItemService

* createItem()
* getActiveItems()
* getItemDetail()

---

### AuthService

* register()
* login()

---

## 4. Mapper（MyBatis）

Mapper 僅負責資料存取，不包含業務邏輯。

功能範圍：

* insert
* update
* select
* delete

---

常用 Mapper：

* BidMapper
* ItemMapper
* UserMapper
* AuctionResultMapper

---

## 5. 自動結標機制

透過排程任務定期檢查拍賣狀態：

```java
@Scheduled(fixedRate = 60000)
```

---

### AuctionScheduler

每分鐘執行一次：

```sql
SELECT item
WHERE end_time < now
AND status = ACTIVE
```

並呼叫：

```
auctionService.closeAuction(item)
```

---

## 6. 自動寄信流程

系統完成結標後執行通知流程：

```
Scheduler
   ↓
AuctionService.closeAuction()
   ↓
查詢最高出價
   ↓
寫入 auc
```
