const API_BASE = '/api/apps';
let currentAppId = null;

// DOM元素
const appListSection = document.getElementById('appListSection');
const createAppSection = document.getElementById('createAppSection');
const configManageSection = document.getElementById('configManageSection');
const appsTableBody = document.getElementById('appsTableBody');
const createAppForm = document.getElementById('createAppForm');
const appListLink = document.getElementById('appListLink');
const createAppLink = document.getElementById('createAppLink');
const refreshAppsBtn = document.getElementById('refreshAppsBtn');
const configEditor = document.getElementById('configEditor');
const saveConfigBtn = document.getElementById('saveConfigBtn');
const configHistoryList = document.getElementById('configHistoryList');
const configAppTitle = document.getElementById('configAppTitle');
const deleteConfirmModal = new bootstrap.Modal('#deleteConfirmModal');

// 初始化页面
document.addEventListener('DOMContentLoaded', () => {
    loadApps();
    setupEventListeners();
});

// 事件监听
function setupEventListeners() {
    appListLink.addEventListener('click', showAppList);
    createAppLink.addEventListener('click', showCreateForm);
    refreshAppsBtn.addEventListener('click', loadApps);
    createAppForm.addEventListener('submit', handleCreateApp);
    saveConfigBtn.addEventListener('click', saveConfig);

    // 动态生成的元素使用事件委托
    document.addEventListener('click', (e) => {
        if (e.target.classList.contains('manage-config-btn')) {
            manageConfig(e.target.dataset.appId, e.target.dataset.appName);
        }
        if (e.target.classList.contains('delete-app-btn')) {
            showDeleteConfirm(e.target.dataset.appId);
        }
        if (e.target.classList.contains('config-version')) {
            loadConfigVersion(e.target.dataset.versionId);
        }
    });
}

// 加载App列表
async function loadApps() {
    try {
        const response = await fetch(API_BASE);
        if (!response.ok) throw new Error('获取应用列表失败');

        const apps = await response.json();
        renderAppsTable(apps);
    } catch (error) {
        console.error('Error:', error);
        alert(error.message);
    }
}

// 渲染App表格
function renderAppsTable(apps) {
    appsTableBody.innerHTML = apps.map(app => `
        <tr>
            <td>${app.id}</td>
            <td>${app.name}</td>
            <td>${new Date(app.createdAt).toLocaleString()}</td>
            <td>${new Date(app.lastUpdatedAt).toLocaleString()}</td>
            <td>
                <button class="btn btn-sm btn-info manage-config-btn" 
                    data-app-id="${app.id}"
                    data-app-name="${app.name}">
                    管理配置
                </button>
                <button class="btn btn-sm btn-danger delete-app-btn" 
                    data-app-id="${app.id}">
                    删除
                </button>
            </td>
        </tr>
    `).join('');
}

// 显示App列表
function showAppList() {
    appListSection.classList.remove('hidden');
    createAppSection.classList.add('hidden');
    configManageSection.classList.add('hidden');
    loadApps();
}

// 显示创建表单
function showCreateForm() {
    appListSection.classList.add('hidden');
    createAppSection.classList.remove('hidden');
    configManageSection.classList.add('hidden');
    createAppForm.reset();
}

// 处理创建App
async function handleCreateApp(e) {
    e.preventDefault();

    const appData = {
        name: document.getElementById('appName').value,
        description: document.getElementById('appDescription').value
    };

    try {
        const response = await fetch(API_BASE, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            },
            body: JSON.stringify(appData)
        });

        if (!response.ok) throw new Error('创建应用失败');

        alert('应用创建成功！');
        showAppList();
    } catch (error) {
        console.error('Error:', error);
        alert(error.message);
    }
}

// 管理配置
async function manageConfig(appId, appName) {
    currentAppId = appId;
    configAppTitle.textContent = `${appName} - 配置管理`;

    try {
        // 获取当前配置
        const configResponse = await fetch(`${API_BASE}/${appId}/config`);
        if (!configResponse.ok) throw new Error('获取配置失败');
        const config = await configResponse.json();
        configEditor.value = JSON.stringify(config, null, 2);

        // 获取配置历史
        const historyResponse = await fetch(`${API_BASE}/${appId}/config/history`);
        if (!historyResponse.ok) throw new Error('获取历史记录失败');
        const history = await historyResponse.json();
        renderConfigHistory(history);

        // 切换视图
        appListSection.classList.add('hidden');
        createAppSection.classList.add('hidden');
        configManageSection.classList.remove('hidden');
    } catch (error) {
        console.error('Error:', error);
        alert(error.message);
    }
}

// 保存配置
async function saveConfig() {
    try {
        const config = JSON.parse(configEditor.value); // 验证JSON格式

        const response = await fetch(`${API_BASE}/${currentAppId}/config`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            },
            body: JSON.stringify(config)
        });

        if (!response.ok) throw new Error('保存配置失败');

        alert('配置保存成功！');
        manageConfig(currentAppId, configAppTitle.textContent.split(' - ')[0]);
    } catch (error) {
        console.error('Error:', error);
        alert(`保存失败: ${error.message}`);
    }
}

// 渲染配置历史
function renderConfigHistory(history) {
    configHistoryList.innerHTML = history.map(item => `
        <li class="list-group-item config-version" data-version-id="${item.version}">
            <div class="d-flex justify-content-between">
                <span>版本 ${item.version}</span>
                <small class="text-muted">${new Date(item.updatedAt).toLocaleString()}</small>
            </div>
            <small class="text-muted">${item.updater}</small>
        </li>
    `).join('');
}

// 加载历史版本
async function loadConfigVersion(versionId) {
    try {
        const response = await fetch(`${API_BASE}/${currentAppId}/config/history/${versionId}`);
        if (!response.ok) throw new Error('获取历史版本失败');

        const config = await response.json();
        configEditor.value = JSON.stringify(config, null, 2);

        // 高亮选中的历史版本
        document.querySelectorAll('.config-version').forEach(el => {
            el.classList.toggle('active', el.dataset.versionId === versionId);
        });
    } catch (error) {
        console.error('Error:', error);
        alert(error.message);
    }
}

// 显示删除确认
function showDeleteConfirm(appId) {
    currentAppId = appId;
    deleteConfirmModal.show();

    document.getElementById('confirmDeleteBtn').addEventListener('click', async () => {
        try {
            const response = await fetch(`${API_BASE}/${appId}`, {
                method: 'DELETE',
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                }
            });

            if (!response.ok) throw new Error('删除应用失败');

            alert('应用删除成功！');
            deleteConfirmModal.hide();
            loadApps();
        } catch (error) {
            console.error('Error:', error);
            alert(error.message);
        }
    }, { once: true });
}
