// 检查登录状态
async function checkAuth() {
    const token = localStorage.getItem('token');
    if (!token) {
        window.location.href = '/login.html';
        return;
    }

    try {
        const response = await fetch('/api/users/me', {
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (!response.ok) throw new Error('认证失败');

        const user = await response.json();
        document.getElementById('usernameDisplay').textContent = user.data.username;
        document.getElementById('userNav').classList.remove('collapse');
    } catch (error) {
        console.error('Auth error:', error);
        localStorage.removeItem('token');
        window.location.href = '/login.html';
    }
}

// 注销处理
document.getElementById('logoutBtn')?.addEventListener('click', async () => {
    try {
        await fetch('/api/users/logout', {
            method: 'POST',
            headers: { 'Authorization': `Bearer ${localStorage.getItem('token')}` }
        });

        localStorage.removeItem('token');
        window.location.href = '/login.html';
    } catch (error) {
        console.error('Logout error:', error);
    }
});

// 页面加载时检查认证
document.addEventListener('DOMContentLoaded', checkAuth);
