/**
 * Utility functions để làm việc với authentication
 */
const AuthUtils = {
    /**
     * Lưu token vào localStorage
     * @param {Object} tokenData - Object chứa thông tin token
     */
    saveToken: function(tokenData) {
        if (!tokenData || !tokenData.accessToken) {
            console.error('Invalid token data');
            return;
        }
        
        localStorage.setItem('token', tokenData.accessToken);
        localStorage.setItem('tokenType', tokenData.tokenType || 'Bearer');
        
        if (tokenData.expiresIn) {
            const expiryTime = Date.now() + parseInt(tokenData.expiresIn);
            localStorage.setItem('tokenExpiry', expiryTime);
        }
    },
    
    /**
     * Lấy token từ localStorage
     * @returns {Object} Token data object
     */
    getToken: function() {
        const token = localStorage.getItem('token');
        if (!token) return null;
        
        return {
            accessToken: token,
            tokenType: localStorage.getItem('tokenType') || 'Bearer',
            expiresIn: localStorage.getItem('expiresIn'),
            expiryTime: localStorage.getItem('tokenExpiry')
        };
    },
    
    /**
     * Kiểm tra user đã đăng nhập chưa
     * @returns {Boolean} 
     */
    isAuthenticated: function() {
        const token = this.getToken();
        if (!token) return false;
        
        // Kiểm tra token expiry
        if (token.expiryTime) {
            const now = Date.now();
            if (now > parseInt(token.expiryTime)) {
                this.logout();
                return false;
            }
        }
        
        return true;
    },
    
    /**
     * Đăng xuất - xóa token
     */
    logout: function() {
        localStorage.removeItem('token');
        localStorage.removeItem('tokenType');
        localStorage.removeItem('expiresIn');
        localStorage.removeItem('tokenExpiry');
    },
    
    /**
     * Lấy authorization header
     * @returns {String} Authorization header value
     */
    getAuthHeader: function() {
        const token = this.getToken();
        if (!token) return null;
        
        return `${token.tokenType} ${token.accessToken}`;
    },
    
    /**
     * Đăng nhập với Google
     * @returns {Promise} Promise resolves khi đăng nhập thành công
     */
    loginWithGoogle: function() {
        return this.loginWithOAuth('/oauth2/authorization/google');
    },
    
    /**
     * Đăng nhập với GitHub
     * @returns {Promise} Promise resolves khi đăng nhập thành công
     */
    loginWithGithub: function() {
        return this.loginWithOAuth('/oauth2/authorization/github');
    },
    
    /**
     * Thực hiện OAuth login
     * @param {String} url - OAuth authorization URL
     * @returns {Promise} Promise resolves khi đăng nhập thành công
     */
    loginWithOAuth: function(url) {
        return new Promise((resolve, reject) => {
            const width = 600;
            const height = 600;
            const left = window.innerWidth / 2 - width / 2;
            const top = window.innerHeight / 2 - height / 2;
            
            // Thêm redirect_uri vào URL nếu cần
            let authUrl = url;
            const currentOrigin = '10.10.100.90:8081';
            
            // Thêm redirect_uri vào URL
            if (url.indexOf('?') !== -1) {
                authUrl += `&redirect_uri=${encodeURIComponent(currentOrigin)}`;
            } else {
                authUrl += `?redirect_uri=${encodeURIComponent(currentOrigin)}`;
            }
            
            const popup = window.open(authUrl, 'oauth2',
                `width=${width},height=${height},top=${top},left=${left}`);
            
            if (!popup) {
                reject(new Error('Popup blocked. Please allow popups for this site.'));
                return;
            }
            
            // Theo dõi xem popup có bị đóng không
            const checkPopupClosed = setInterval(() => {
                if (popup.closed) {
                    clearInterval(checkPopupClosed);
                    if (!this.isAuthenticated()) {
                        reject(new Error('Authentication was cancelled or failed.'));
                    } else {
                        resolve(this.getToken());
                    }
                }
            }, 500);
            
            // Lắng nghe tin nhắn từ popup
            const handleMessage = (event) => {
                // Trong môi trường development, cho phép nhận message từ bất kỳ origin nào
                // Trong production, bạn nên kiểm tra origin nghiêm ngặt hơn
                console.log('Received message from:', event.origin);
                
                const data = event.data;
                if (data && data.accessToken) {
                    // Lưu token vào localStorage
                    this.saveToken(data);
                    
                    // Cleanup
                    window.removeEventListener('message', handleMessage);
                    clearInterval(checkPopupClosed);
                    
                    // Resolve promise
                    resolve(data);
                }
            };
            
            window.addEventListener('message', handleMessage);
        });
    },
    
    /**
     * Gọi API với authentication
     * @param {String} endpoint - API endpoint
     * @param {Object} options - Fetch options
     * @returns {Promise} Promise resolves với API response
     */
    callApi: async function(endpoint, options = {}) {
        if (!this.isAuthenticated()) {
            throw new Error('Not authenticated');
        }
        
        const headers = {
            'Content-Type': 'application/json',
            'Authorization': this.getAuthHeader(),
            ...options.headers
        };
        
        try {
            const response = await fetch(endpoint, {
                ...options,
                headers
            });
            
            if (!response.ok) {
                if (response.status === 401) {
                    this.logout();
                    throw new Error('Token expired. Please login again.');
                }
                
                const errorData = await response.json().catch(() => ({}));
                throw new Error(errorData.message || `API error: ${response.status}`);
            }
            
            if (response.status === 204) {
                return null;
            }
            
            return await response.json();
        } catch (error) {
            console.error('API call error:', error);
            throw error;
        }
    }
};

// Export cho ES modules
if (typeof module !== 'undefined') {
    module.exports = AuthUtils;
} 