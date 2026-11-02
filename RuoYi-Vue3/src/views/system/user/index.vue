<template>
  <list-page class="app-container user-page">
    <header class="user-page-heading">
      <h1>用户管理</h1>
      <el-button type="primary" icon="Plus" @click="handleAdd" v-hasPermi="['system:user:add']">新增用户</el-button>
    </header>
    <div class="tree-sidebar-manage-wrap user-management">
    <tree-panel title="组织机构" :tree-data="deptOptions" search-placeholder="请输入部门名称"
                storage-key="dept-sidebar-width" :defaultExpandAll="true" @node-click="handleNodeClick"
                @refresh="getDeptTree" ref="deptTreeRef"/>
    <div class="tree-sidebar-content">
      <div class="content-inner">
        <el-form :model="queryParams" ref="queryRef" class="user-filters" v-show="showSearch" label-position="top">
          <el-form-item label="用户名称" prop="userName">
            <el-input v-model="queryParams.userName" placeholder="请输入用户名称" clearable
                      @keyup.enter="handleQuery"/>
          </el-form-item>
          <el-form-item label="手机号码" prop="phonenumber">
            <el-input v-model="queryParams.phonenumber" placeholder="请输入手机号码" clearable
                      @keyup.enter="handleQuery"/>
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-select v-model="queryParams.status" placeholder="全部状态" clearable>
              <el-option v-for="dict in sys_normal_disable" :key="dict.value" :label="dict.label" :value="dict.value"/>
            </el-select>
          </el-form-item>
          <el-form-item class="filter-actions">
            <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
            <el-button icon="Refresh" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>

        <div class="user-list-heading">
          <h2>用户列表 <span>{{ total }} 位用户</span></h2>
          <span v-if="ids.length" class="selection-count" role="status">已选择 {{ ids.length }} 项</span>
        </div>
        <el-row :gutter="10" class="mb8 user-toolbar">
          <el-col :span="1.5">
            <el-button icon="Edit" :disabled="single" @click="handleUpdate"
                       v-hasPermi="['system:user:edit']">修改
            </el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete"
                       v-hasPermi="['system:user:remove']">删除
            </el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button icon="Upload" @click="handleImport" v-hasPermi="['system:user:import']">导入
            </el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button icon="Download" @click="handleExport" v-hasPermi="['system:user:export']">
              导出
            </el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" :columns="columns"
                         storageKey="xxxxxxxx"></right-toolbar>
        </el-row>

        <el-table v-loading="loading" :data="userList" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="50" align="center"/>
          <el-table-column label="用户编号" align="center" key="userId" prop="userId" width="96" v-if="columns.userId.visible"/>
          <el-table-column label="用户名称" align="left" key="userName" min-width="120" v-if="columns.userName.visible"
                           :show-overflow-tooltip="true">
            <template #default="scope">
              <el-button link type="primary" @click="handleViewData(scope.row)">{{ scope.row.userName }}</el-button>
            </template>
          </el-table-column>
          <el-table-column label="用户昵称" align="left" key="nickName" prop="nickName" min-width="120"
                           v-if="columns.nickName.visible" :show-overflow-tooltip="true"/>
          <el-table-column label="部门" align="left" key="deptName" prop="dept.deptName" min-width="120"
                           v-if="columns.deptName.visible" :show-overflow-tooltip="true"/>
          <el-table-column label="手机号码" align="center" key="phonenumber" prop="phonenumber"
                           v-if="columns.phonenumber.visible" width="120"/>
          <el-table-column label="状态" align="center" key="status" width="80" v-if="columns.status.visible">
            <template #default="scope">
              <el-switch
                  v-model="scope.row.status"
                  active-value="0"
                  inactive-value="1"
                  :aria-label="scope.row.userName + '的账户状态'"
                  @change="handleStatusChange(scope.row)"
              ></el-switch>
            </template>
          </el-table-column>
          <el-table-column label="创建时间" align="center" prop="createTime" v-if="columns.createTime.visible"
                           width="160">
            <template #default="scope">
              <span>{{ parseTime(scope.row.createTime) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center" width="150" class-name="small-padding fixed-width">
            <template #default="scope">
              <el-tooltip content="修改" placement="top" v-if="scope.row.userId !== 1">
                <el-button link type="primary" icon="Edit" aria-label="修改用户" @click="handleUpdate(scope.row)"
                           v-hasPermi="['system:user:edit']"></el-button>
              </el-tooltip>
              <el-tooltip content="删除" placement="top" v-if="scope.row.userId !== 1">
                <el-button link type="primary" icon="Delete" aria-label="删除用户" @click="handleDelete(scope.row)"
                           v-hasPermi="['system:user:remove']"></el-button>
              </el-tooltip>
              <el-tooltip content="重置密码" placement="top" v-if="scope.row.userId !== 1">
                <el-button link type="primary" icon="Key" aria-label="重置密码" @click="handleResetPwd(scope.row)"
                           v-hasPermi="['system:user:resetPwd']"></el-button>
              </el-tooltip>
              <el-tooltip content="分配角色" placement="top" v-if="scope.row.userId !== 1">
                <el-button link type="primary" icon="CircleCheck" aria-label="分配角色" @click="handleAuthRole(scope.row)"
                           v-hasPermi="['system:user:edit']"></el-button>
              </el-tooltip>
            </template>
          </el-table-column>
        </el-table>

      </div>
    </div>

    <!-- 添加或修改用户配置对话框 -->
    </div>
    <el-dialog :title="title" v-model="open" width="600px" append-to-body>
      <el-form :model="form" :rules="rules" ref="userRef" label-width="80px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="用户昵称" prop="nickName">
              <el-input v-model="form.nickName" placeholder="请输入用户昵称" maxlength="30"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="归属部门" prop="deptId">
              <el-tree-select v-model="form.deptId" :data="enabledDeptOptions"
                              :props="{ value: 'id', label: 'label', children: 'children' }" value-key="id"
                              placeholder="请选择归属部门" clearable check-strictly/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="手机号码" prop="phonenumber">
              <el-input v-model="form.phonenumber" placeholder="请输入手机号码" maxlength="11"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" maxlength="50"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item v-if="form.userId == undefined" label="用户名称" prop="userName">
              <el-input v-model="form.userName" placeholder="请输入用户名称" maxlength="30"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item v-if="form.userId == undefined" label="用户密码" prop="password" :rules="pwdValidator">
              <el-input v-model="form.password" placeholder="请输入用户密码" type="password" maxlength="20"
                        show-password/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="用户性别">
              <el-select v-model="form.sex" placeholder="请选择">
                <el-option v-for="dict in sys_user_sex" :key="dict.value" :label="dict.label"
                           :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-radio-group v-model="form.status">
                <el-radio v-for="dict in sys_normal_disable" :key="dict.value" :value="dict.value">{{ dict.label }}
                </el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="角色">
              <el-select v-model="form.roleIds" multiple placeholder="请选择">
                <el-option v-for="item in roleOptions" :key="item.roleId" :label="item.roleName" :value="item.roleId"
                           :disabled="item.status == 1"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input v-model="form.remark" type="textarea" placeholder="请输入内容"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

      <!-- 用户详情抽屉 -->
      <user-view-drawer ref="userViewRef"/>
      <!-- 用户导入对话框 -->
      <excel-import-dialog ref="importUserRef" title="用户导入" action="/system/user/importData"
                           template-action="/system/user/importTemplate" template-file-name="user_template"
                           update-support-label="是否更新已经存在的用户数据" @success="getList"/>
    <template #footer>
      <pagination
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
      />
    </template>
  </list-page>
</template>

<script setup name="User">
import TreePanel from "@/components/TreePanel"
import ExcelImportDialog from "@/components/ExcelImportDialog"
import UserViewDrawer from "./view"
import {usePasswordRule} from "@/utils/passwordRule"
import {
  changeUserStatus,
  listUser,
  resetUserPwd,
  delUser,
  getUser,
  updateUser,
  addUser,
  deptTreeSelect
} from "@/api/system/user"

const router = useRouter()
const {proxy} = getCurrentInstance()
const {pwdValidator, pwdPromptValidator} = usePasswordRule()
const {sys_normal_disable, sys_user_sex} = useDict("sys_normal_disable", "sys_user_sex")

const userList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")
const deptOptions = ref(undefined)
const enabledDeptOptions = ref(undefined)
const initPassword = ref(undefined)
const roleOptions = ref([])
// 列显隐信息
const columns = ref({
  userId: {label: '用户编号', visible: true},
  userName: {label: '用户名称', visible: true},
  nickName: {label: '用户昵称', visible: true},
  deptName: {label: '部门', visible: true},
  phonenumber: {label: '手机号码', visible: true},
  status: {label: '状态', visible: true},
  createTime: {label: '创建时间', visible: true}
})

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 50,
    userName: undefined,
    phonenumber: undefined,
    status: undefined,
    deptId: undefined
  },
  rules: {
    userName: [{required: true, message: "用户名称不能为空", trigger: "blur"}, {
      min: 2,
      max: 20,
      message: "用户名称长度必须介于 2 和 20 之间",
      trigger: "blur"
    }],
    nickName: [{required: true, message: "用户昵称不能为空", trigger: "blur"}],
    email: [{type: "email", message: "请输入正确的邮箱地址", trigger: ["blur", "change"]}],
    phonenumber: [{pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/, message: "请输入正确的手机号码", trigger: "blur"}]
  }
})

const {queryParams, form, rules} = toRefs(data)

/** 查询用户列表 */
function getList() {
  loading.value = true
  listUser(queryParams.value).then(res => {
    loading.value = false
    userList.value = res.rows
    total.value = res.total
  })
}

/** 查询部门下拉树结构 */
function getDeptTree() {
  deptTreeSelect().then(response => {
    deptOptions.value = response.data
    enabledDeptOptions.value = filterDisabledDept(JSON.parse(JSON.stringify(response.data)))
  })
}

/** 过滤禁用的部门 */
function filterDisabledDept(deptList) {
  return deptList.filter(dept => {
    if (dept.disabled) {
      return false
    }
    if (dept.children && dept.children.length) {
      dept.children = filterDisabledDept(dept.children)
    }
    return true
  })
}

/** 节点单击事件 */
function handleNodeClick(data) {
  queryParams.value.deptId = data.id
  handleQuery()
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef")
  queryParams.value.deptId = undefined
  proxy.$refs.deptTreeRef.setCurrentKey(null)
  handleQuery()
}

/** 删除按钮操作 */
function handleDelete(row) {
  const userIds = row.userId || ids.value
  proxy.$modal.confirm('是否确认删除用户编号为"' + userIds + '"的数据项？').then(function () {
    return delUser(userIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {
  })
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download("system/user/export", {
    ...queryParams.value,
  }, `user_${new Date().getTime()}.xlsx`)
}

/** 用户状态修改  */
function handleStatusChange(row) {
  let text = row.status === "0" ? "启用" : "停用"
  proxy.$modal.confirm('确认要"' + text + '""' + row.userName + '"用户吗?').then(function () {
    return changeUserStatus(row.userId, row.status)
  }).then(() => {
    proxy.$modal.msgSuccess(text + "成功")
  }).catch(function () {
    row.status = row.status === "0" ? "1" : "0"
  })
}

/** 更多操作 */
function handleCommand(command, row) {
  switch (command) {
    case "handleResetPwd":
      handleResetPwd(row)
      break
    case "handleAuthRole":
      handleAuthRole(row)
      break
    default:
      break
  }
}

/** 跳转角色分配 */
function handleAuthRole(row) {
  const userId = row.userId
  router.push("/system/user-auth/role/" + userId)
}

/** 重置密码按钮操作 */
function handleResetPwd(row) {
  proxy.$prompt(`请输入「${row.userName}」的新密码`, "重置密码", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    closeOnClickModal: false,
    inputValidator: pwdPromptValidator
  }).then(({value}) => {
    resetUserPwd(row.userId, value).then(() => {
      proxy.$modal.msgSuccess("修改成功，新密码是：" + value)
    })
  }).catch(() => {
  })
}

/** 选择条数  */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.userId)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 详情按钮操作 */
function handleViewData(row) {
  proxy.$refs["userViewRef"].open(row.userId)
}

/** 导入按钮操作 */
function handleImport() {
  proxy.$refs["importUserRef"].open()
}

/** 重置操作表单 */
function reset() {
  form.value = {
    userId: undefined,
    deptId: undefined,
    userName: undefined,
    nickName: undefined,
    password: undefined,
    phonenumber: undefined,
    email: undefined,
    sex: undefined,
    status: "0",
    remark: undefined,
    roleIds: []
  }
  proxy.resetForm("userRef")
}

/** 取消按钮 */
function cancel() {
  open.value = false
  reset()
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  getUser().then(response => {
    roleOptions.value = response.roles
    open.value = true
    title.value = "添加用户"
    form.value.password = initPassword.value
  })
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const userId = row.userId || ids.value
  getUser(userId).then(response => {
    form.value = response.data
    roleOptions.value = response.roles
    form.value.roleIds = response.roleIds
    open.value = true
    title.value = "修改用户"
    form.value.password = ""
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["userRef"].validate(valid => {
    if (valid) {
      if (form.value.userId != undefined) {
        updateUser(form.value).then(() => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addUser(form.value).then(() => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
}

onMounted(() => {
  getDeptTree()
  getList()
  proxy.getConfigKey("sys.user.initPassword").then(response => {
    initPassword.value = response.msg
  })
})
</script>

<style scoped lang="scss">
.user-page {
  padding: 16px 24px;
  color: var(--el-text-color-primary);

  :deep(.list-page__content) {
    display: flex;
    flex-direction: column;
  }
}

.user-page-heading {
  display: flex;
  flex-shrink: 0;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 16px;

  h1 {
    margin: 0;
    font-size: 20px;
    line-height: 1.4;
    letter-spacing: -0.5px;
    font-weight: 600;
  }

  > .el-button { flex-shrink: 0; min-height: 40px; }
}

.user-management {
  flex: 1 0 auto;
  min-height: 560px;
  background: var(--workspace-surface);
  border: 1px solid var(--workspace-border);
  border-radius: var(--workspace-radius);
}

.tree-sidebar-content { background: var(--workspace-surface); }
.tree-sidebar-content .content-inner { padding: 24px; }
.user-management :deep(.tree-sidebar) { background: var(--workspace-surface); border-color: var(--workspace-border); }
.user-management :deep(.tree-header) { background: var(--workspace-surface); border-color: var(--workspace-border); min-height: 64px; }
.user-management :deep(.tree-title) { color: var(--el-text-color-primary); font-size: 14px; }
.user-management :deep(.el-tree-node__content) { min-height: 36px; }

.user-filters {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr)) auto;
  gap: 16px;
  padding-bottom: 24px;
  margin-bottom: 24px;
  border-bottom: 1px solid var(--workspace-border);

  :deep(.el-form-item) { min-width: 0; margin: 0; }
  :deep(.el-form-item__label) { padding: 0; margin-bottom: 8px; font-size: 12px; line-height: 1.5; font-weight: 500; color: var(--el-text-color-regular); }
  :deep(.el-input), :deep(.el-select) { width: 100%; min-width: 0; }
  :deep(.el-form-item__content) { min-width: 0; }
  .filter-actions { align-self: end; }
  :deep(.el-button) { min-height: 36px; }
}

.user-list-heading {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 16px;

  h2 { margin: 0; font-size: 15px; line-height: 1.5; font-weight: 600; }
  h2 span { margin-left: 8px; font-size: 12px; color: var(--el-text-color-secondary); font-weight: 400; font-variant-numeric: tabular-nums; }
}

.selection-count { font-size: 12px; color: var(--el-color-primary); font-variant-numeric: tabular-nums; }
.user-toolbar { row-gap: 8px; margin-bottom: 16px; }
.user-management :deep(.el-table) { font-variant-numeric: tabular-nums; }
.user-management :deep(.el-table__cell) { padding-top: 12px; padding-bottom: 12px; }
.user-management :deep(.el-table__header-wrapper th) { background-color: var(--workspace-inset) !important; color: var(--el-text-color-secondary); font-size: 12px; }
.user-management :deep(.el-table__empty-block) { min-height: 200px; }
.user-management :deep(.el-button:focus-visible) { outline: 2px solid var(--el-color-primary); outline-offset: 2px; }

@media (max-width: 1279px) {
  .tree-sidebar-content .content-inner { padding: 20px; }
  .user-filters { grid-template-columns: repeat(2, minmax(0, 1fr)); }
}

@media (max-width: 767px) {
  .user-page { padding: 16px; }
  .user-management { flex-direction: column; min-height: 0; }
  .user-management :deep(.tree-sidebar) { width: 100% !important; max-height: 240px; border-right: 0; border-bottom: 1px solid var(--workspace-border); }
  .user-management :deep(.tree-sidebar.collapsed) { max-height: 48px; }
  .user-management :deep(.tree-sidebar.collapsed .tree-header) { min-height: 48px; }
  .user-management :deep(.resize-handle) { display: none; }
  .user-management :deep(.collapse-button-container) { top: 24px; right: 8px; width: 24px; background: var(--workspace-surface); box-shadow: none; }
  .user-management :deep(.tree-header) { padding-right: 40px; }
  .tree-sidebar-content .content-inner { padding: 16px; }
  .user-filters { grid-template-columns: minmax(0, 1fr); }
  .user-filters .filter-actions { grid-column: 1 / -1; }
  .user-filters :deep(.el-input__wrapper), .user-filters :deep(.el-select__wrapper), .user-filters :deep(.el-button) { min-height: 40px; }
  .user-toolbar :deep(.el-button) { min-height: 40px; min-width: 40px; }
}
</style>
