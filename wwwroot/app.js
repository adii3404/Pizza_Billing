// Domino's Pizza POS & Billing System - Frontend for .NET 8 Web API
(function() {
  const state = {
    currentUser: { id: 2, username: 'aditya', role: 'manager', fullName: 'Aditya Nimbalkar' },
    currentView: 'ORDER',
    pizzas: [],
    orders: [],
    users: [],
    dailySales: [],
    monthlySales: [],
    topSelling: [],
    // POS Cart & Form State
    custName: '',
    selectedCategory: 'All',
    searchQuery: '',
    orderItems: [],
    activePizza: null,
    activeSize: 'Medium',
    activeQty: 1,
    selectedToppings: [],
    selectedDrinks: [],
    discountPercent: 0,
    paymentMode: 'Cash',
    upiId: '',
    showQrModal: false,
    activeReceipt: null,
    notification: null,
    // Product form
    productForm: { pizzaId: 0, name: '', category: 'Classic Pizza', smallPrice: 99, mediumPrice: 199, largePrice: 399, xlPrice: 499, quantity: 20 },
    // Staff form
    managerForm: { username: '', fullName: '', password: '' }
  };

  const API = {
    async getPizzas() { const res = await fetch('/api/pizzas'); return await res.json(); },
    async getOrders() { const res = await fetch('/api/orders'); return await res.json(); },
    async getUsers() { const res = await fetch('/api/users'); return await res.json(); },
    async getDailySales() { const res = await fetch('/api/reports/daily-sales'); return await res.json(); },
    async getMonthlySales() { const res = await fetch('/api/reports/monthly-sales'); return await res.json(); },
    async getTopSelling() { const res = await fetch('/api/reports/top-selling'); return await res.json(); },
    async placeOrder(orderReq) {
      const res = await fetch('/api/orders', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(orderReq)
      });
      if (!res.ok) {
        const err = await res.json();
        throw new Error(err.error || 'Failed to place order');
      }
      return await res.json();
    },
    async cancelOrder(id) {
      const res = await fetch(`/api/orders/${id}/cancel`, { method: 'POST' });
      return res.ok;
    },
    async deleteOrder(id) {
      const res = await fetch(`/api/orders/${id}`, { method: 'DELETE' });
      return res.ok;
    },
    async updateStock(id, qty) {
      const res = await fetch(`/api/pizzas/${id}/stock`, {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(qty)
      });
      return res.ok;
    },
    async addPizza(pizza) {
      const res = await fetch('/api/pizzas', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(pizza)
      });
      return await res.json();
    },
    async updatePizza(id, pizza) {
      const res = await fetch(`/api/pizzas/${id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(pizza)
      });
      return res.ok;
    },
    async deletePizza(id) {
      const res = await fetch(`/api/pizzas/${id}`, { method: 'DELETE' });
      return res.ok;
    },
    async addManager(username, fullName) {
      const res = await fetch('/api/users/manager', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, fullName })
      });
      return res.ok;
    },
    async deleteManager(id) {
      const res = await fetch(`/api/users/manager/${id}`, { method: 'DELETE' });
      return res.ok;
    }
  };

  async function loadData() {
    try {
      const [pizzas, orders, users, dailySales, monthlySales, topSelling] = await Promise.all([
        API.getPizzas(),
        API.getOrders(),
        API.getUsers(),
        API.getDailySales(),
        API.getMonthlySales(),
        API.getTopSelling()
      ]);
      state.pizzas = pizzas || [];
      state.orders = orders || [];
      state.users = users || [];
      state.dailySales = dailySales || [];
      state.monthlySales = monthlySales || [];
      state.topSelling = topSelling || [];
      render();
    } catch (e) {
      console.error('Failed to load initial data:', e);
    }
  }

  function showNotify(type, message) {
    state.notification = { type, message };
    render();
    setTimeout(() => {
      state.notification = null;
      render();
    }, 3500);
  }

  function getPriceForSize(pizza, size) {
    switch (size) {
      case 'Small': return pizza.smallPrice;
      case 'Medium': return pizza.mediumPrice;
      case 'Large': return pizza.largePrice;
      case 'ExtraLarge': return pizza.extraLargePrice;
      default: return pizza.mediumPrice;
    }
  }

  function renderNavbar() {
    if (!state.currentUser) return '';
    const isAdmin = state.currentUser.role === 'admin';

    const tabs = [
      { id: 'ORDER', label: 'POS Terminal', icon: '🛍️' },
      { id: 'STOCK', label: 'Inventory Stock', icon: '📦' },
      { id: 'DAILY_SALE', label: 'Daily Sales', icon: '📊' },
      { id: 'MONTHLY_SALE', label: 'Monthly Sales', icon: '📅', adminOnly: true },
      { id: 'TOP_SELLING', label: 'Top Pizzas', icon: '🔥' },
      { id: 'PRODUCT', label: 'Menu Catalog', icon: '🍕', adminOnly: true },
      { id: 'MANAGER', label: 'Staff Accounts', icon: '👥', adminOnly: true },
      { id: 'ORDER_HISTORY', label: 'Order History', icon: '📜' },
    ];

    return `
      <header class="bg-[#006491] text-white shadow-lg sticky top-0 z-40 select-none">
        <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div class="flex items-center justify-between h-16 border-b border-white/10">
            <div class="flex items-center space-x-3">
              <div class="w-10 h-10 bg-white rounded-xl shadow-md flex items-center justify-center font-black text-xl text-[#006491] border-2 border-white/40">
                🍕
              </div>
              <div>
                <div class="flex items-center gap-2">
                  <span class="font-black text-lg tracking-wider text-white">DOMINO'S</span>
                  <span class="text-[10px] px-2 py-0.5 rounded-full font-bold uppercase tracking-wider bg-[#E31837] text-white shadow-sm">
                    .NET 8 POS
                  </span>
                </div>
                <p class="text-xs text-blue-200 hidden sm:block">
                  Pizza Billing &amp; Restaurant Terminal
                </p>
              </div>
            </div>

            <div class="flex items-center space-x-4">
              <div class="flex items-center gap-2 bg-[#004b6e] px-3 py-1.5 rounded-xl border border-white/10 text-xs">
                <span class="text-amber-300 font-bold">👤</span>
                <div class="text-left">
                  <p class="font-bold text-white leading-none">${state.currentUser.fullName || state.currentUser.username}</p>
                  <p class="text-blue-200 uppercase text-[10px] font-semibold">${state.currentUser.role}</p>
                </div>
              </div>

              <button onclick="window.dominosLogout()" class="px-3 py-1.5 bg-[#E31837] hover:bg-[#c0142d] text-white rounded-xl text-xs font-bold transition-all shadow-sm cursor-pointer">
                Logout
              </button>
            </div>
          </div>

          <div class="flex space-x-1 overflow-x-auto py-2">
            ${tabs
              .filter(t => !t.adminOnly || isAdmin)
              .map(t => `
                <button
                  onclick="window.dominosSetView('${t.id}')"
                  class="flex items-center gap-2 px-3.5 py-2 rounded-xl text-xs font-bold whitespace-nowrap transition-all cursor-pointer ${
                    state.currentView === t.id ? 'bg-white text-[#006491] shadow-md' : 'text-blue-100 hover:bg-white/10 hover:text-white'
                  }">
                  <span>${t.icon}</span>
                  <span>${t.label}</span>
                </button>
              `).join('')}
          </div>
        </div>
      </header>
    `;
  }

  function renderLoginPage() {
    return `
      <div class="min-h-screen flex items-center justify-center bg-[#f4f7f6] p-4 relative">
        <div class="w-full max-w-md bg-white rounded-2xl shadow-xl border border-gray-200 overflow-hidden relative z-10">
          <div class="bg-[#006491] p-6 text-center text-white">
            <div class="w-16 h-16 mx-auto mb-3 bg-white text-[#006491] rounded-2xl flex items-center justify-center font-black text-3xl shadow-lg border-2 border-white/20">
              🍕
            </div>
            <h1 class="text-2xl font-black tracking-wider uppercase">DOMINO'S POS</h1>
            <p class="text-blue-100 text-xs mt-1">.NET 8 Enterprise Billing &amp; Store System</p>
          </div>

          <form onsubmit="window.dominosHandleLogin(event)" class="p-8 space-y-4">
            <div>
              <label class="block text-xs font-bold uppercase tracking-wider text-gray-600 mb-1">Username</label>
              <input id="loginUsername" type="text" value="aditya" class="w-full px-3.5 py-2.5 bg-gray-50 border border-gray-300 rounded-xl text-sm font-medium focus:ring-2 focus:ring-[#006491]" required>
            </div>

            <div>
              <label class="block text-xs font-bold uppercase tracking-wider text-gray-600 mb-1">Password</label>
              <input id="loginPassword" type="password" value="••••••••" class="w-full px-3.5 py-2.5 bg-gray-50 border border-gray-300 rounded-xl text-sm font-medium focus:ring-2 focus:ring-[#006491]" required>
            </div>

            <div>
              <label class="block text-xs font-bold uppercase tracking-wider text-gray-600 mb-1">Role</label>
              <select id="loginRole" class="w-full px-3.5 py-2.5 bg-gray-50 border border-gray-300 rounded-xl text-sm font-medium focus:ring-2 focus:ring-[#006491]">
                <option value="manager" selected>Store Manager</option>
                <option value="admin">Store Administrator</option>
              </select>
            </div>

            <button type="submit" class="w-full py-3 bg-[#006491] hover:bg-[#00557c] text-white font-bold rounded-xl shadow-lg text-sm uppercase tracking-wider cursor-pointer">
              Sign In to POS Terminal
            </button>

            <div class="pt-4 border-t border-gray-200">
              <p class="text-xs font-semibold text-gray-500 mb-2">Authorized Staff Quick Access:</p>
              <div class="grid grid-cols-2 gap-2">
                <button type="button" onclick="window.dominosQuickLogin('aditya', 'manager', 'Aditya Nimbalkar')" class="py-1.5 px-2 bg-blue-50 text-[#006491] rounded-lg text-xs font-semibold border border-blue-200 truncate cursor-pointer">
                  Aditya Nimbalkar (Mgr)
                </button>
                <button type="button" onclick="window.dominosQuickLogin('prathamesh', 'manager', 'Prathamesh')" class="py-1.5 px-2 bg-emerald-50 text-emerald-800 rounded-lg text-xs font-semibold border border-emerald-200 truncate cursor-pointer">
                  Prathamesh (Staff)
                </button>
                <button type="button" onclick="window.dominosQuickLogin('vishwajit', 'manager', 'Vishwajit')" class="py-1.5 px-2 bg-purple-50 text-purple-800 rounded-lg text-xs font-semibold border border-purple-200 truncate cursor-pointer">
                  Vishwajit (Staff)
                </button>
                <button type="button" onclick="window.dominosQuickLogin('admin', 'admin', 'System Administrator')" class="py-1.5 px-2 bg-red-50 text-[#E31837] rounded-lg text-xs font-semibold border border-red-200 truncate cursor-pointer">
                  Store Admin
                </button>
              </div>
            </div>
          </form>
        </div>
      </div>
    `;
  }

  function renderPOS() {
    const filteredPizzas = state.pizzas.filter(p => {
      const matchCat = state.selectedCategory === 'All' || p.category === state.selectedCategory;
      const matchSearch = p.name.toLowerCase().includes(state.searchQuery.toLowerCase());
      return matchCat && matchSearch;
    });

    const subtotal = state.orderItems.reduce((acc, curr) => acc + curr.itemTotal, 0);
    const discountAmount = Number((subtotal * (state.discountPercent / 100)).toFixed(2));
    const discountedSubtotal = subtotal - discountAmount;
    const cgst = Number((discountedSubtotal * 0.025).toFixed(2));
    const sgst = Number((discountedSubtotal * 0.025).toFixed(2));
    const totalAmount = Number((discountedSubtotal + cgst + sgst).toFixed(2));

    return `
      <div class="max-w-7xl mx-auto p-4 sm:p-6 lg:p-8">
        <div class="grid grid-cols-1 lg:grid-cols-12 gap-6 items-start">
          <!-- Left: Menu Grid & Customer (8 cols) -->
          <div class="lg:col-span-8 space-y-6">
            <div class="bg-white rounded-2xl p-5 shadow-sm border border-gray-200">
              <div class="flex flex-col sm:flex-row gap-4 justify-between items-center">
                <div class="w-full sm:w-1/2">
                  <label class="block text-xs font-bold uppercase tracking-wider text-gray-500 mb-1">
                    Customer Name <span class="text-[#E31837]">*</span>
                  </label>
                  <input
                    type="text"
                    value="${state.custName}"
                    oninput="window.dominosSetCustName(this.value)"
                    placeholder="Enter customer name"
                    class="w-full px-3.5 py-2.5 bg-gray-50 border border-gray-300 rounded-xl text-sm font-medium focus:ring-2 focus:ring-[#006491]"
                  />
                </div>

                <div class="w-full sm:w-1/2">
                  <label class="block text-xs font-bold uppercase tracking-wider text-gray-500 mb-1">
                    Search Menu
                  </label>
                  <input
                    type="text"
                    value="${state.searchQuery}"
                    oninput="window.dominosSetSearch(this.value)"
                    placeholder="Search pizzas (e.g. Farmhouse)..."
                    class="w-full px-3.5 py-2.5 bg-gray-50 border border-gray-300 rounded-xl text-sm focus:ring-2 focus:ring-[#006491]"
                  />
                </div>
              </div>

              <!-- Categories -->
              <div class="flex gap-2 mt-4 overflow-x-auto pb-1">
                ${['All', 'Classic Pizza', 'Pan Pizza', 'Oil Pizza'].map(cat => `
                  <button
                    onclick="window.dominosSetCategory('${cat}')"
                    class="px-4 py-2 rounded-xl text-xs font-bold tracking-wide transition-all cursor-pointer whitespace-nowrap ${
                      state.selectedCategory === cat ? 'bg-[#006491] text-white shadow-md' : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
                    }">
                    ${cat}
                  </button>
                `).join('')}
              </div>
            </div>

            <!-- Pizza Cards -->
            <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4">
              ${filteredPizzas.map(pizza => {
                const isOut = pizza.quantity <= 0;
                return `
                  <div
                    onclick="window.dominosOpenCustomize(${pizza.pizzaId})"
                    class="bg-white rounded-2xl p-4 border transition-all flex flex-col justify-between cursor-pointer group ${
                      isOut ? 'opacity-60 border-red-200 bg-red-50/20' : 'border-gray-200 hover:border-[#006491] hover:shadow-lg'
                    }">
                    <div>
                      <div class="w-full h-32 rounded-xl bg-gradient-to-tr from-amber-50 to-orange-100 flex items-center justify-center relative overflow-hidden mb-3 border border-amber-100">
                        <img src="/default_pizza.png" alt="${pizza.name}" class="w-24 h-24 object-contain drop-shadow-md group-hover:scale-105 transition-transform" />
                        <span class="absolute top-2 left-2 px-2 py-0.5 rounded-md text-[10px] font-bold uppercase bg-white/90 text-[#006491]">
                          ${pizza.category}
                        </span>
                        <span class="absolute top-2 right-2 px-2 py-0.5 rounded-md text-[10px] font-bold ${
                          isOut ? 'bg-[#E31837] text-white' : pizza.quantity < 15 ? 'bg-amber-500 text-white' : 'bg-emerald-600 text-white'
                        }">
                          ${isOut ? 'Out of Stock' : `${pizza.quantity} in stock`}
                        </span>
                      </div>
                      <h3 class="font-bold text-gray-900 text-base group-hover:text-[#006491]">${pizza.name}</h3>
                      <p class="text-xs text-gray-500 line-clamp-1 mt-0.5">${pizza.description || 'Oven-baked pizza'}</p>
                    </div>

                    <div class="mt-4 pt-3 border-t border-gray-100 flex items-center justify-between">
                      <div>
                        <span class="text-[10px] text-gray-400 uppercase font-semibold block">Starts from</span>
                        <span class="font-black text-base text-[#006491]">₹${pizza.smallPrice.toFixed(2)}</span>
                      </div>
                      <button class="px-3 py-1.5 rounded-xl text-xs font-bold ${
                        isOut ? 'bg-gray-200 text-gray-400' : 'bg-[#006491] text-white shadow-sm'
                      }">
                        + Select
                      </button>
                    </div>
                  </div>
                `;
              }).join('')}
            </div>

            <!-- Prepared Items -->
            <div class="bg-white rounded-2xl p-5 shadow-sm border border-gray-200">
              <div class="flex items-center justify-between mb-4">
                <h2 class="font-bold text-gray-800 text-base">
                  Orders Prepared for Billing (${state.orderItems.length})
                </h2>
                ${state.orderItems.length > 0 ? `
                  <button onclick="window.dominosClearItems()" class="text-xs text-[#E31837] hover:underline font-semibold cursor-pointer">
                    Clear items
                  </button>
                ` : ''}
              </div>

              ${state.orderItems.length === 0 ? `
                <div class="py-8 text-center text-gray-400 text-sm border-2 border-dashed border-gray-200 rounded-xl">
                  Click any pizza above to choose size, toppings, drinks &amp; add here.
                </div>
              ` : `
                <div class="divide-y divide-gray-100">
                  ${state.orderItems.map((item, idx) => `
                    <div class="py-3 flex items-center justify-between gap-4">
                      <div class="flex-1">
                        <div class="flex items-center gap-2">
                          <span class="font-bold text-gray-900 text-sm">${item.pizzaName}</span>
                          <span class="px-2 py-0.5 rounded text-[10px] font-bold bg-blue-50 text-[#006491]">${item.size}</span>
                          <span class="text-xs text-gray-500">× ${item.quantity}</span>
                        </div>
                        <div class="text-xs text-gray-500 mt-1 flex flex-wrap gap-2">
                          ${item.toppings.length ? `<span class="bg-amber-50 text-amber-800 px-2 py-0.5 rounded text-[11px]">+ Toppings: ${item.toppings.join(', ')}</span>` : ''}
                          ${item.coldDrinks.length ? `<span class="bg-emerald-50 text-emerald-800 px-2 py-0.5 rounded text-[11px]">+ Drinks: ${item.coldDrinks.join(', ')}</span>` : ''}
                        </div>
                      </div>
                      <div class="flex items-center gap-3">
                        <span class="font-black text-gray-800 text-sm">₹${item.itemTotal.toFixed(2)}</span>
                        <button onclick="window.dominosRemoveItem(${idx})" class="text-gray-400 hover:text-[#E31837] p-1.5 cursor-pointer">
                          ✕
                        </button>
                      </div>
                    </div>
                  `).join('')}
                </div>
              `}
            </div>
          </div>

          <!-- Right: Billing Sidebar (4 cols) -->
          <div class="lg:col-span-4 sticky top-24">
            <div class="bg-[#004b6e] text-white rounded-2xl shadow-xl overflow-hidden border border-blue-900">
              <div class="p-5 border-b border-white/10 bg-[#003d59] flex items-center justify-between">
                <div>
                  <h2 class="font-black text-lg tracking-wider uppercase">ORDER BILLING</h2>
                  <p class="text-xs text-blue-200">Cashier: <b>${state.currentUser.fullName || state.currentUser.username}</b></p>
                </div>
                <span class="px-2.5 py-1 bg-[#E31837] text-white rounded-lg text-xs font-black">
                  ${state.orderItems.length} ITEMS
                </span>
              </div>

              <div class="p-5 space-y-4">
                <div class="flex justify-between items-center text-sm">
                  <span class="text-blue-200">Subtotal:</span>
                  <span class="font-bold text-base">₹${subtotal.toFixed(2)}</span>
                </div>

                <div class="flex justify-between items-center text-sm">
                  <span class="text-blue-200">Discount (%):</span>
                  <input
                    type="number"
                    min="0"
                    max="100"
                    value="${state.discountPercent}"
                    oninput="window.dominosSetDiscount(this.value)"
                    class="w-20 px-2.5 py-1.5 bg-[#00344d] border border-blue-700 rounded-lg text-right text-white font-bold text-sm"
                  />
                </div>

                ${state.discountPercent > 0 ? `
                  <div class="flex justify-between items-center text-xs text-emerald-300">
                    <span>Discount savings:</span>
                    <span>-₹${discountAmount.toFixed(2)}</span>
                  </div>
                ` : ''}

                <div class="border-t border-white/10 pt-3 space-y-2 text-xs">
                  <div class="flex justify-between items-center text-blue-200">
                    <span>CGST (2.5%):</span>
                    <span class="font-semibold text-white">₹${cgst.toFixed(2)}</span>
                  </div>
                  <div class="flex justify-between items-center text-blue-200">
                    <span>SGST (2.5%):</span>
                    <span class="font-semibold text-white">₹${sgst.toFixed(2)}</span>
                  </div>
                </div>

                <div class="border-t-2 border-dashed border-white/20 pt-4 pb-2 flex justify-between items-baseline">
                  <span class="text-sm font-bold uppercase tracking-wider text-blue-200">Grand Total:</span>
                  <span class="text-2xl font-black text-amber-300">₹${totalAmount.toFixed(2)}</span>
                </div>

                <!-- Payment Modes -->
                <div class="space-y-2 pt-2">
                  <label class="block text-xs font-bold uppercase tracking-wider text-blue-200">Payment Mode</label>
                  <div class="grid grid-cols-3 gap-2">
                    ${['Cash', 'Online', 'UPI'].map(mode => `
                      <button
                        onclick="window.dominosSetPaymentMode('${mode}')"
                        class="py-2 rounded-xl text-xs font-bold cursor-pointer transition-all ${
                          state.paymentMode === mode ? 'bg-[#E31837] text-white shadow-md' : 'bg-[#00344d] text-blue-200 hover:bg-[#003d59]'
                        }">
                        ${mode}
                      </button>
                    `).join('')}
                  </div>
                </div>

                ${state.paymentMode === 'UPI' ? `
                  <div class="p-3 bg-[#00344d] rounded-xl border border-blue-700/60 space-y-3">
                    <input
                      type="text"
                      value="${state.upiId}"
                      oninput="window.dominosSetUpiId(this.value)"
                      placeholder="customer@upi"
                      class="w-full px-3 py-1.5 bg-[#00283d] border border-blue-600 rounded-lg text-xs text-white"
                    />
                    <button onclick="window.dominosToggleQr(true)" class="w-full py-2 bg-blue-600 hover:bg-blue-500 text-white rounded-lg text-xs font-bold cursor-pointer">
                      Scan UPI QR Code
                    </button>
                  </div>
                ` : ''}

                <!-- Actions -->
                <div class="space-y-2 pt-2">
                  <button
                    onclick="window.dominosFinalizeOrder()"
                    ${state.orderItems.length === 0 ? 'disabled' : ''}
                    class="w-full py-3.5 rounded-xl font-black text-sm uppercase tracking-wider transition-all shadow-lg cursor-pointer ${
                      state.orderItems.length === 0 ? 'bg-gray-500 text-gray-300 opacity-60 cursor-not-allowed' : 'bg-[#E31837] hover:bg-[#c0142d] text-white'
                    }">
                    FINALIZE ORDER
                  </button>

                  ${state.orders.length > 0 ? `
                    <button onclick="window.dominosShowReceipt(0)" class="w-full py-2.5 bg-emerald-700 hover:bg-emerald-600 text-white rounded-xl text-xs font-bold cursor-pointer">
                      VIEW LAST INVOICE (PDF)
                    </button>
                  ` : ''}

                  <button onclick="window.dominosResetPos()" class="w-full py-2 bg-[#00344d] text-blue-200 hover:text-white rounded-xl text-xs font-medium cursor-pointer">
                    Reset Form
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    `;
  }

  function renderStock() {
    return `
      <div class="max-w-7xl mx-auto p-4 sm:p-6 lg:p-8 space-y-6">
        <div class="bg-white p-6 rounded-2xl border border-gray-200 shadow-xs flex justify-between items-center">
          <div>
            <h1 class="text-xl font-black uppercase tracking-wider text-gray-900">AVAILABLE STOCK &amp; INVENTORY</h1>
            <p class="text-xs text-gray-500 mt-1">Real-time inventory levels backed by ASP.NET Core thread-safe storage</p>
          </div>
          <div class="px-4 py-2 bg-emerald-50 border border-emerald-200 rounded-xl text-center">
            <span class="block text-[10px] uppercase font-bold text-emerald-800">Total Stock</span>
            <span class="text-lg font-black text-emerald-800">${state.pizzas.reduce((a, b) => a + b.quantity, 0)} Units</span>
          </div>
        </div>

        <div class="bg-white rounded-2xl border border-gray-200 shadow-xs overflow-hidden">
          <table class="w-full text-left border-collapse text-xs">
            <thead>
              <tr class="bg-[#006491] text-white uppercase text-[11px] font-bold">
                <th class="py-3.5 px-4">ID</th>
                <th class="py-3.5 px-4">Pizza Name</th>
                <th class="py-3.5 px-4">Category</th>
                <th class="py-3.5 px-4 text-right">Small</th>
                <th class="py-3.5 px-4 text-right">Medium</th>
                <th class="py-3.5 px-4 text-right">Large</th>
                <th class="py-3.5 px-4 text-right">XL</th>
                <th class="py-3.5 px-4 text-center">Quantity</th>
                <th class="py-3.5 px-4 text-center">Status</th>
                <th class="py-3.5 px-4 text-center">Restock</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-100 font-medium text-gray-700">
              ${state.pizzas.map(p => `
                <tr class="hover:bg-blue-50/40">
                  <td class="py-3 px-4 font-bold text-gray-500">#${p.pizzaId}</td>
                  <td class="py-3 px-4 font-bold text-gray-900">${p.name}</td>
                  <td class="py-3 px-4">${p.category}</td>
                  <td class="py-3 px-4 text-right">₹${p.smallPrice.toFixed(2)}</td>
                  <td class="py-3 px-4 text-right">₹${p.mediumPrice.toFixed(2)}</td>
                  <td class="py-3 px-4 text-right">₹${p.largePrice.toFixed(2)}</td>
                  <td class="py-3 px-4 text-right">₹${p.extraLargePrice.toFixed(2)}</td>
                  <td class="py-3 px-4 text-center font-black text-sm">${p.quantity}</td>
                  <td class="py-3 px-4 text-center">
                    <span class="px-2.5 py-1 rounded-full text-[10px] font-bold ${
                      p.quantity <= 0 ? 'bg-red-100 text-[#E31837]' : p.quantity < 15 ? 'bg-amber-100 text-amber-800' : 'bg-emerald-100 text-emerald-800'
                    }">
                      ${p.quantity <= 0 ? 'Out of Stock' : p.quantity < 15 ? 'Low Stock' : 'In Stock'}
                    </span>
                  </td>
                  <td class="py-3 px-4 text-center">
                    <button onclick="window.dominosAddStock(${p.pizzaId}, 10)" class="px-2 py-1 bg-blue-50 text-[#006491] rounded font-bold hover:bg-[#006491] hover:text-white cursor-pointer">+10</button>
                    <button onclick="window.dominosAddStock(${p.pizzaId}, 25)" class="px-2 py-1 bg-blue-50 text-[#006491] rounded font-bold hover:bg-[#006491] hover:text-white cursor-pointer">+25</button>
                  </td>
                </tr>
              `).join('')}
            </tbody>
          </table>
        </div>
      </div>
    `;
  }

  function renderDailySales() {
    const totalRev = state.dailySales.reduce((a, b) => a + b.totalEarnings, 0);

    return `
      <div class="max-w-7xl mx-auto p-4 sm:p-6 lg:p-8 space-y-6">
        <div class="bg-white p-6 rounded-2xl border border-gray-200 shadow-xs flex justify-between items-center">
          <div>
            <h1 class="text-xl font-black uppercase tracking-wider text-gray-900">DAILY SALES REPORT</h1>
            <p class="text-xs text-gray-500 mt-1">Aggregated order revenue grouped by calendar date and payment method</p>
          </div>
          <div class="px-5 py-3 bg-blue-50 border border-blue-200 rounded-2xl text-right">
            <span class="block text-[10px] uppercase font-bold text-[#006491]">Daily Earnings Total</span>
            <span class="text-2xl font-black text-[#006491]">₹${totalRev.toFixed(2)}</span>
          </div>
        </div>

        <div class="bg-white rounded-2xl border border-gray-200 shadow-xs overflow-hidden">
          <table class="w-full text-left border-collapse text-xs">
            <thead>
              <tr class="bg-[#006491] text-white uppercase text-[11px] font-bold">
                <th class="py-3.5 px-6">Order Date</th>
                <th class="py-3.5 px-6">Payment Channel</th>
                <th class="py-3.5 px-6 text-center">Completed Orders</th>
                <th class="py-3.5 px-6 text-right">Total Earnings (₹)</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-100 font-medium text-gray-700">
              ${state.dailySales.map(r => `
                <tr class="hover:bg-blue-50/40">
                  <td class="py-3.5 px-6 font-bold text-gray-900">${r.orderDay}</td>
                  <td class="py-3.5 px-6 font-semibold">${r.paymentMode}</td>
                  <td class="py-3.5 px-6 text-center">${r.orderCount} orders</td>
                  <td class="py-3.5 px-6 text-right font-black text-sm text-[#006491]">₹${r.totalEarnings.toFixed(2)}</td>
                </tr>
              `).join('')}
            </tbody>
          </table>
        </div>
      </div>
    `;
  }

  function renderMonthlySales() {
    const totalRev = state.monthlySales.reduce((a, b) => a + b.totalEarnings, 0);

    return `
      <div class="max-w-7xl mx-auto p-4 sm:p-6 lg:p-8 space-y-6">
        <div class="bg-white p-6 rounded-2xl border border-gray-200 shadow-xs flex justify-between items-center">
          <div>
            <h1 class="text-xl font-black uppercase tracking-wider text-gray-900">MONTHLY SALES REPORT</h1>
            <p class="text-xs text-gray-500 mt-1">Aggregated revenue ledger grouped by fiscal month</p>
          </div>
          <div class="px-5 py-3 bg-blue-50 border border-blue-200 rounded-2xl text-right">
            <span class="block text-[10px] uppercase font-bold text-[#006491]">Gross Monthly Revenue</span>
            <span class="text-2xl font-black text-[#006491]">₹${totalRev.toFixed(2)}</span>
          </div>
        </div>

        <div class="bg-white rounded-2xl border border-gray-200 shadow-xs overflow-hidden">
          <table class="w-full text-left border-collapse text-xs">
            <thead>
              <tr class="bg-[#006491] text-white uppercase text-[11px] font-bold">
                <th class="py-3.5 px-6">Order Month</th>
                <th class="py-3.5 px-6">Payment Mode</th>
                <th class="py-3.5 px-6 text-center">Orders</th>
                <th class="py-3.5 px-6 text-right">Total Earnings (₹)</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-100 font-medium text-gray-700">
              ${state.monthlySales.map(r => `
                <tr class="hover:bg-blue-50/40">
                  <td class="py-3.5 px-6 font-bold text-gray-900">${r.orderMonth}</td>
                  <td class="py-3.5 px-6 font-semibold">${r.paymentMode}</td>
                  <td class="py-3.5 px-6 text-center">${r.orderCount} orders</td>
                  <td class="py-3.5 px-6 text-right font-black text-sm text-[#006491]">₹${r.totalEarnings.toFixed(2)}</td>
                </tr>
              `).join('')}
            </tbody>
          </table>
        </div>
      </div>
    `;
  }

  function renderTopSelling() {
    return `
      <div class="max-w-7xl mx-auto p-4 sm:p-6 lg:p-8 space-y-6">
        <div class="bg-white p-6 rounded-2xl border border-gray-200 shadow-xs flex justify-between items-center">
          <div>
            <h1 class="text-xl font-black uppercase tracking-wider text-gray-900">TOP SELLING PIZZAS REPORT</h1>
            <p class="text-xs text-gray-500 mt-1">Product leaderboard ranked by units sold and gross earnings</p>
          </div>
          <span class="px-4 py-2 bg-amber-50 text-amber-800 rounded-xl text-xs font-bold border border-amber-200">
            🏆 Bestseller Leaderboard
          </span>
        </div>

        <!-- Top 3 Podium -->
        ${state.topSelling.length >= 3 ? `
          <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div class="bg-white p-5 rounded-2xl border text-center">
              <span class="text-2xl">🥈</span>
              <span class="text-[10px] uppercase font-bold text-gray-400 block mt-1">#2 Bestseller</span>
              <h3 class="font-bold text-gray-900 text-base mt-1">${state.topSelling[1].pizzaName}</h3>
              <p class="text-xs text-gray-500">${state.topSelling[1].category}</p>
              <div class="mt-3 pt-3 border-t flex justify-around text-xs">
                <span>Sold: <b>${state.topSelling[1].totalSold}</b></span>
                <span class="text-[#006491]">₹${state.topSelling[1].totalRevenue.toFixed(2)}</span>
              </div>
            </div>

            <div class="bg-gradient-to-b from-amber-50 to-white p-6 rounded-2xl border-2 border-amber-300 text-center shadow-md">
              <span class="text-3xl">🥇</span>
              <span class="text-[11px] uppercase font-black text-amber-600 block mt-1">#1 Top Selling Pizza</span>
              <h3 class="font-black text-gray-900 text-lg mt-1">${state.topSelling[0].pizzaName}</h3>
              <p class="text-xs text-gray-600">${state.topSelling[0].category}</p>
              <div class="mt-4 pt-3 border-t border-amber-100 flex justify-around text-xs">
                <span>Sold: <b>${state.topSelling[0].totalSold} units</b></span>
                <span class="text-[#006491] font-black">₹${state.topSelling[0].totalRevenue.toFixed(2)}</span>
              </div>
            </div>

            <div class="bg-white p-5 rounded-2xl border text-center">
              <span class="text-2xl">🥉</span>
              <span class="text-[10px] uppercase font-bold text-gray-400 block mt-1">#3 Bestseller</span>
              <h3 class="font-bold text-gray-900 text-base mt-1">${state.topSelling[2].pizzaName}</h3>
              <p class="text-xs text-gray-500">${state.topSelling[2].category}</p>
              <div class="mt-3 pt-3 border-t flex justify-around text-xs">
                <span>Sold: <b>${state.topSelling[2].totalSold}</b></span>
                <span class="text-[#006491]">₹${state.topSelling[2].totalRevenue.toFixed(2)}</span>
              </div>
            </div>
          </div>
        ` : ''}

        <div class="bg-white rounded-2xl border border-gray-200 shadow-xs overflow-hidden">
          <table class="w-full text-left border-collapse text-xs">
            <thead>
              <tr class="bg-[#006491] text-white uppercase text-[11px] font-bold">
                <th class="py-3.5 px-6 text-center w-16">Rank</th>
                <th class="py-3.5 px-6">Pizza Name</th>
                <th class="py-3.5 px-6">Category</th>
                <th class="py-3.5 px-6 text-center">Total Units Sold</th>
                <th class="py-3.5 px-6 text-right">Total Revenue</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-100 font-medium text-gray-700">
              ${state.topSelling.map((p, idx) => `
                <tr class="hover:bg-blue-50/40">
                  <td class="py-3.5 px-6 text-center font-black">${idx === 0 ? '🥇 1' : idx === 1 ? '🥈 2' : idx === 2 ? '🥉 3' : `#${idx + 1}`}</td>
                  <td class="py-3.5 px-6 font-bold text-gray-900 text-sm">${p.pizzaName}</td>
                  <td class="py-3.5 px-6">${p.category}</td>
                  <td class="py-3.5 px-6 text-center font-bold text-sm">${p.totalSold}</td>
                  <td class="py-3.5 px-6 text-right font-black text-sm text-[#006491]">₹${p.totalRevenue.toFixed(2)}</td>
                </tr>
              `).join('')}
            </tbody>
          </table>
        </div>
      </div>
    `;
  }

  function renderStaff() {
    const managers = state.users.filter(u => u.role === 'manager');

    return `
      <div class="max-w-7xl mx-auto p-4 sm:p-6 lg:p-8 space-y-6">
        <div class="bg-white p-6 rounded-2xl border border-gray-200 shadow-xs flex justify-between items-center">
          <div>
            <h1 class="text-xl font-black uppercase tracking-wider text-gray-900">STAFF &amp; MANAGER DIRECTORY</h1>
            <p class="text-xs text-gray-500 mt-1">Authorized personnel managing store operations &amp; terminal POS billing</p>
          </div>
          <div class="px-4 py-2 bg-blue-50 border border-blue-200 rounded-xl text-center">
            <span class="block text-[10px] uppercase font-bold text-[#006491]">Staff Count</span>
            <span class="text-lg font-black text-[#006491]">${managers.length}</span>
          </div>
        </div>

        <div class="grid grid-cols-1 lg:grid-cols-12 gap-6 items-start">
          <div class="lg:col-span-5 bg-white p-6 rounded-2xl border border-gray-200 shadow-xs space-y-4">
            <h2 class="text-sm font-black uppercase tracking-wider text-[#006491] border-b pb-2">
              Register New Floor Staff
            </h2>
            <form onsubmit="window.dominosAddStaff(event)" class="space-y-4">
              <div>
                <label class="block text-xs font-bold text-gray-700 uppercase mb-1">Full Name</label>
                <input id="staffFullName" type="text" placeholder="e.g. Aditya Nimbalkar" class="w-full px-3.5 py-2 bg-gray-50 border border-gray-300 rounded-xl text-xs" required />
              </div>
              <div>
                <label class="block text-xs font-bold text-gray-700 uppercase mb-1">Username</label>
                <input id="staffUsername" type="text" placeholder="e.g. aditya" class="w-full px-3.5 py-2 bg-gray-50 border border-gray-300 rounded-xl text-xs" required />
              </div>
              <button type="submit" class="w-full py-3 bg-[#006491] hover:bg-[#00557c] text-white font-bold rounded-xl text-xs uppercase tracking-wider cursor-pointer">
                Add Staff Member
              </button>
            </form>
          </div>

          <div class="lg:col-span-7 bg-white rounded-2xl border border-gray-200 shadow-xs overflow-hidden">
            <div class="p-4 border-b border-gray-200 bg-gray-50">
              <span class="text-xs font-bold uppercase tracking-wider text-gray-700">Active Shift Personnel (${managers.length})</span>
            </div>
            <div class="divide-y divide-gray-100">
              ${managers.map(m => `
                <div class="p-4 flex items-center justify-between hover:bg-gray-50">
                  <div class="flex items-center gap-3">
                    <div class="w-10 h-10 rounded-full bg-blue-100 text-[#006491] flex items-center justify-center font-bold text-sm">
                      ${m.fullName ? m.fullName.charAt(0) : 'U'}
                    </div>
                    <div>
                      <h3 class="font-bold text-gray-900 text-sm">${m.fullName || m.username}</h3>
                      <p class="text-xs text-gray-500 font-mono">@${m.username}</p>
                    </div>
                  </div>
                  <div class="flex items-center gap-3">
                    <span class="px-2.5 py-1 rounded-full text-[10px] font-bold bg-emerald-100 text-emerald-800">
                      ${m.username === 'aditya' ? 'Store Manager' : 'Floor Staff'}
                    </span>
                    ${m.username !== 'aditya' ? `
                      <button onclick="window.dominosDeleteStaff(${m.id})" class="p-2 text-gray-400 hover:text-[#E31837] cursor-pointer">✕</button>
                    ` : ''}
                  </div>
                </div>
              `).join('')}
            </div>
          </div>
        </div>
      </div>
    `;
  }

  function renderOrderHistory() {
    return `
      <div class="max-w-7xl mx-auto p-4 sm:p-6 lg:p-8 space-y-6">
        <div class="bg-white p-6 rounded-2xl border border-gray-200 shadow-xs flex justify-between items-center">
          <div>
            <h1 class="text-xl font-black uppercase tracking-wider text-gray-900">ORDER AUDIT HISTORY</h1>
            <p class="text-xs text-gray-500 mt-1">Browse all placed orders, reprint tax invoices, and process cancellations</p>
          </div>
          <div class="px-4 py-2 bg-blue-50 border border-blue-200 rounded-xl text-center">
            <span class="block text-[10px] uppercase font-bold text-[#006491]">Orders Logged</span>
            <span class="text-lg font-black text-[#006491]">${state.orders.length}</span>
          </div>
        </div>

        <div class="bg-white rounded-2xl border border-gray-200 shadow-xs overflow-hidden">
          <table class="w-full text-left border-collapse text-xs">
            <thead>
              <tr class="bg-[#006491] text-white uppercase text-[11px] font-bold">
                <th class="py-3.5 px-4">Order ID</th>
                <th class="py-3.5 px-4">Customer Name</th>
                <th class="py-3.5 px-4">Date</th>
                <th class="py-3.5 px-4">Items Summary</th>
                <th class="py-3.5 px-4">Payment</th>
                <th class="py-3.5 px-4 text-right">Total (₹)</th>
                <th class="py-3.5 px-4 text-center">Status</th>
                <th class="py-3.5 px-4 text-center">Actions</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-100 font-medium text-gray-700">
              ${state.orders.map(order => {
                const isCancelled = order.status === 'Cancelled';
                return `
                  <tr class="hover:bg-blue-50/40 ${isCancelled ? 'bg-red-50/30 opacity-70' : ''}">
                    <td class="py-3.5 px-4 font-bold text-gray-900">#${order.orderId}</td>
                    <td class="py-3.5 px-4 font-bold text-gray-900">${order.customerName}</td>
                    <td class="py-3.5 px-4 font-mono text-gray-500 text-[11px]">${order.orderDate.substring(0, 19).replace('T', ' ')}</td>
                    <td class="py-3.5 px-4 max-w-xs truncate">
                      ${order.items.map(i => `${i.pizzaName} (${i.size[0]}×${i.quantity})`).join(', ')}
                    </td>
                    <td class="py-3.5 px-4">
                      <span class="px-2 py-0.5 rounded text-[10px] font-bold ${
                        order.paymentMode === 2 || order.paymentMode === 'UPI' ? 'bg-blue-100 text-blue-800' : 'bg-emerald-100 text-emerald-800'
                      }">
                        ${order.paymentMode === 0 || order.paymentMode === 'Cash' ? 'Cash' : order.paymentMode === 1 || order.paymentMode === 'Online' ? 'Online' : 'UPI'}
                      </span>
                    </td>
                    <td class="py-3.5 px-4 text-right font-black text-sm text-[#006491]">₹${order.totalPrice.toFixed(2)}</td>
                    <td class="py-3.5 px-4 text-center">
                      <span class="px-2 py-0.5 rounded text-[10px] font-bold ${isCancelled ? 'bg-red-100 text-[#E31837]' : 'bg-emerald-100 text-emerald-800'}">
                        ${order.status}
                      </span>
                    </td>
                    <td class="py-3.5 px-4 text-center">
                      <button onclick="window.dominosShowReceiptById(${order.orderId})" class="px-2 py-1 bg-blue-50 text-[#006491] rounded font-bold cursor-pointer hover:bg-[#006491] hover:text-white">Receipt</button>
                      ${!isCancelled ? `
                        <button onclick="window.dominosCancelOrder(${order.orderId})" class="px-2 py-1 bg-amber-50 text-amber-800 rounded font-bold cursor-pointer hover:bg-amber-600 hover:text-white">Cancel</button>
                      ` : ''}
                    </td>
                  </tr>
                `;
              }).join('')}
            </tbody>
          </table>
        </div>
      </div>
    `;
  }

  function renderProducts() {
    return `
      <div class="max-w-7xl mx-auto p-4 sm:p-6 lg:p-8 space-y-6">
        <div class="bg-white p-6 rounded-2xl border border-gray-200 shadow-xs flex justify-between items-center">
          <div>
            <h1 class="text-xl font-black uppercase tracking-wider text-gray-900">MENU CATALOG MANAGEMENT</h1>
            <p class="text-xs text-gray-500 mt-1">Configure pizza recipes, multi-tier pricing and catalog offerings</p>
          </div>
        </div>

        <div class="grid grid-cols-1 lg:grid-cols-12 gap-6 items-start">
          <div class="lg:col-span-5 bg-white p-6 rounded-2xl border border-gray-200 shadow-xs space-y-3">
            <h2 class="text-sm font-black uppercase tracking-wider text-[#006491] border-b pb-2">Add New Pizza</h2>
            <form onsubmit="window.dominosAddProduct(event)" class="space-y-3">
              <div>
                <label class="block text-xs font-bold text-gray-700 uppercase mb-1">Pizza Name</label>
                <input id="prodName" type="text" placeholder="e.g. Cheese Burst Treat" class="w-full px-3 py-2 bg-gray-50 border rounded-xl text-xs" required />
              </div>
              <div>
                <label class="block text-xs font-bold text-gray-700 uppercase mb-1">Category</label>
                <select id="prodCategory" class="w-full px-3 py-2 bg-gray-50 border rounded-xl text-xs">
                  <option value="Classic Pizza">Classic Pizza</option>
                  <option value="Pan Pizza">Pan Pizza</option>
                  <option value="Oil Pizza">Oil Pizza</option>
                </select>
              </div>
              <div class="grid grid-cols-2 gap-2">
                <div>
                  <label class="block text-[11px] font-bold text-gray-600 uppercase mb-1">Small Price (₹)</label>
                  <input id="prodSmall" type="number" step="0.01" value="129" class="w-full px-3 py-1.5 bg-gray-50 border rounded-xl text-xs font-bold" required />
                </div>
                <div>
                  <label class="block text-[11px] font-bold text-gray-600 uppercase mb-1">Medium Price (₹)</label>
                  <input id="prodMedium" type="number" step="0.01" value="239" class="w-full px-3 py-1.5 bg-gray-50 border rounded-xl text-xs font-bold" required />
                </div>
                <div>
                  <label class="block text-[11px] font-bold text-gray-600 uppercase mb-1">Large Price (₹)</label>
                  <input id="prodLarge" type="number" step="0.01" value="449" class="w-full px-3 py-1.5 bg-gray-50 border rounded-xl text-xs font-bold" required />
                </div>
                <div>
                  <label class="block text-[11px] font-bold text-gray-600 uppercase mb-1">XL Price (₹)</label>
                  <input id="prodXL" type="number" step="0.01" value="569" class="w-full px-3 py-1.5 bg-gray-50 border rounded-xl text-xs font-bold" required />
                </div>
              </div>
              <div>
                <label class="block text-xs font-bold text-gray-700 uppercase mb-1">Initial Quantity</label>
                <input id="prodQty" type="number" value="25" class="w-full px-3 py-2 bg-gray-50 border rounded-xl text-xs font-bold" required />
              </div>
              <button type="submit" class="w-full py-2.5 bg-[#006491] text-white rounded-xl text-xs font-bold uppercase tracking-wider cursor-pointer">
                + Add to Menu
              </button>
            </form>
          </div>

          <div class="lg:col-span-7 bg-white rounded-2xl border border-gray-200 shadow-xs overflow-hidden">
            <div class="p-4 border-b border-gray-200 bg-gray-50">
              <span class="text-xs font-bold uppercase tracking-wider text-gray-700">Catalog Menu Items (${state.pizzas.length})</span>
            </div>
            <div class="overflow-x-auto max-h-[500px]">
              <table class="w-full text-left border-collapse text-xs">
                <thead>
                  <tr class="bg-[#006491] text-white uppercase text-[10px] font-bold">
                    <th class="py-2.5 px-3">ID</th>
                    <th class="py-2.5 px-3">Name</th>
                    <th class="py-2.5 px-3">Category</th>
                    <th class="py-2.5 px-3 text-right">Medium</th>
                    <th class="py-2.5 px-3 text-center">Stock</th>
                    <th class="py-2.5 px-3 text-center">Action</th>
                  </tr>
                </thead>
                <tbody class="divide-y divide-gray-100">
                  ${state.pizzas.map(p => `
                    <tr class="hover:bg-blue-50/50">
                      <td class="py-2.5 px-3 font-bold text-gray-500">#${p.pizzaId}</td>
                      <td class="py-2.5 px-3 font-bold text-gray-900">${p.name}</td>
                      <td class="py-2.5 px-3">${p.category}</td>
                      <td class="py-2.5 px-3 text-right font-bold text-[#006491]">₹${p.mediumPrice.toFixed(2)}</td>
                      <td class="py-2.5 px-3 text-center font-bold">${p.quantity}</td>
                      <td class="py-2.5 px-3 text-center">
                        <button onclick="window.dominosDeleteProduct(${p.pizzaId})" class="text-[#E31837] hover:underline font-bold cursor-pointer">Delete</button>
                      </td>
                    </tr>
                  `).join('')}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    `;
  }

  function renderReceiptModal() {
    if (!state.activeReceipt) return '';
    const r = state.activeReceipt;

    return `
      <div class="fixed inset-0 z-50 flex items-center justify-center bg-black/60 p-4 backdrop-blur-xs overflow-y-auto">
        <div class="bg-white rounded-2xl max-w-md w-full shadow-2xl border border-gray-200 overflow-hidden relative my-8">
          <div class="p-4 bg-gray-50 border-b flex items-center justify-between print:hidden">
            <span class="font-bold text-xs uppercase tracking-wider text-gray-700">Tax Invoice Receipt</span>
            <button onclick="window.dominosCloseReceipt()" class="text-gray-400 hover:text-gray-600 p-1 cursor-pointer">✕</button>
          </div>

          <div id="printable-receipt" class="p-6 bg-[#fffdf9] font-mono text-xs text-gray-800 space-y-4">
            <div class="text-center border-b border-dashed border-gray-400 pb-4 space-y-1">
              <h2 class="text-lg font-black tracking-wider text-[#006491]">DOMINO'S PIZZA</h2>
              <p class="text-[10px] text-gray-500">Fast Casual Pizza &amp; Delivery Services</p>
              <p class="text-[10px] text-gray-500">GSTIN: 27AADCP1234F1Z9</p>
            </div>

            <div class="space-y-1 text-[11px] border-b border-dashed border-gray-400 pb-3">
              <div class="flex justify-between">
                <span class="text-gray-500">Order ID:</span>
                <span class="font-bold text-gray-900">#${r.orderId}</span>
              </div>
              <div class="flex justify-between">
                <span class="text-gray-500">Customer:</span>
                <span class="font-bold text-gray-900">${r.customerName}</span>
              </div>
              <div class="flex justify-between">
                <span class="text-gray-500">Order Date:</span>
                <span>${r.orderDate.substring(0, 19).replace('T', ' ')}</span>
              </div>
              <div class="flex justify-between">
                <span class="text-gray-500">Cashier:</span>
                <span>${r.cashierUsername}</span>
              </div>
            </div>

            <div class="space-y-2 border-b border-dashed border-gray-400 pb-3">
              <div class="grid grid-cols-12 font-bold text-[10px] text-gray-500 uppercase pb-1 border-b">
                <span class="col-span-6">Item</span>
                <span class="col-span-2 text-center">Qty</span>
                <span class="col-span-4 text-right">Price</span>
              </div>

              ${r.items.map(item => `
                <div class="space-y-0.5 text-[11px]">
                  <div class="grid grid-cols-12 font-semibold text-gray-900">
                    <span class="col-span-6">${item.pizzaName} (${item.size[0]})</span>
                    <span class="col-span-2 text-center">×${item.quantity}</span>
                    <span class="col-span-4 text-right">₹${item.itemTotal.toFixed(2)}</span>
                  </div>
                  ${item.toppings && item.toppings.length ? `<div class="text-[10px] text-gray-500 pl-2">+ Top: ${item.toppings.join(', ')}</div>` : ''}
                  ${item.coldDrinks && item.coldDrinks.length ? `<div class="text-[10px] text-gray-500 pl-2">+ Drink: ${item.coldDrinks.join(', ')}</div>` : ''}
                </div>
              `).join('')}
            </div>

            <div class="space-y-1.5 text-[11px] border-b border-dashed border-gray-400 pb-3">
              <div class="flex justify-between text-gray-600">
                <span>Subtotal</span>
                <span>₹${r.subtotal.toFixed(2)}</span>
              </div>
              ${r.discountPercent > 0 ? `
                <div class="flex justify-between text-emerald-700 font-semibold">
                  <span>Discount (${r.discountPercent}%)</span>
                  <span>-₹${r.discountAmount.toFixed(2)}</span>
                </div>
              ` : ''}
              <div class="flex justify-between text-gray-600">
                <span>CGST (2.5%)</span>
                <span>₹${r.cgst.toFixed(2)}</span>
              </div>
              <div class="flex justify-between text-gray-600">
                <span>SGST (2.5%)</span>
                <span>₹${r.sgst.toFixed(2)}</span>
              </div>
              <div class="flex justify-between text-base font-black pt-2 text-[#006491] border-t border-gray-300">
                <span>GRAND TOTAL</span>
                <span>₹${r.totalPrice.toFixed(2)}</span>
              </div>
            </div>

            <div class="text-center pt-2 space-y-1">
              <p class="font-bold text-[11px] text-gray-700">Thank you for your order!</p>
              <p class="text-[9px] text-gray-400">Powered by Domino's .NET 8 POS Core Engine</p>
            </div>
          </div>

          <div class="p-4 bg-gray-50 border-t flex gap-3 print:hidden">
            <button onclick="window.print()" class="flex-1 py-2.5 bg-[#006491] hover:bg-[#00557c] text-white rounded-xl text-xs font-bold uppercase tracking-wider cursor-pointer">
              Print / Save PDF
            </button>
            <button onclick="window.dominosCloseReceipt()" class="px-4 py-2.5 bg-gray-200 hover:bg-gray-300 text-gray-700 rounded-xl text-xs font-bold cursor-pointer">
              Close
            </button>
          </div>
        </div>
      </div>
    `;
  }

  function renderCustomizeModal() {
    if (!state.activePizza) return '';
    const p = state.activePizza;
    const basePrice = getPriceForSize(p, state.activeSize);
    const itemTotal = (basePrice * state.activeQty) + (state.selectedToppings.length * 40) + (state.selectedDrinks.length * 50);

    return `
      <div class="fixed inset-0 z-50 flex items-center justify-center bg-black/60 p-4 backdrop-blur-xs">
        <div class="bg-white rounded-2xl max-w-lg w-full p-6 shadow-2xl border border-gray-200 space-y-5">
          <div class="flex items-start justify-between border-b pb-3">
            <div>
              <span class="text-[11px] font-bold uppercase text-[#006491]">${p.category}</span>
              <h3 class="text-xl font-black text-gray-900">${p.name}</h3>
              <p class="text-xs text-gray-500">${p.description}</p>
            </div>
            <button onclick="window.dominosCloseCustomize()" class="text-gray-400 hover:text-gray-600 text-xl font-bold cursor-pointer">✕</button>
          </div>

          <!-- Size -->
          <div>
            <label class="block text-xs font-bold uppercase tracking-wider text-gray-700 mb-2">1. Select Size</label>
            <div class="grid grid-cols-4 gap-2">
              ${['Small', 'Medium', 'Large', 'ExtraLarge'].map(size => `
                <button
                  onclick="window.dominosSetSize('${size}')"
                  class="p-2.5 rounded-xl border text-center transition-all cursor-pointer ${
                    state.activeSize === size ? 'border-[#006491] bg-blue-50 text-[#006491] font-bold' : 'border-gray-200 text-gray-600'
                  }">
                  <span class="block text-xs">${size === 'ExtraLarge' ? 'XL' : size}</span>
                  <span class="block text-xs font-black mt-1">₹${getPriceForSize(p, size)}</span>
                </button>
              `).join('')}
            </div>
          </div>

          <!-- Quantity -->
          <div>
            <label class="block text-xs font-bold uppercase tracking-wider text-gray-700 mb-2">2. Quantity</label>
            <div class="flex items-center gap-3">
              <button onclick="window.dominosSetQty(-1)" class="w-9 h-9 rounded-xl bg-gray-100 hover:bg-gray-200 font-bold text-gray-700 cursor-pointer">-</button>
              <span class="w-12 text-center font-black text-lg text-gray-800">${state.activeQty}</span>
              <button onclick="window.dominosSetQty(1)" class="w-9 h-9 rounded-xl bg-gray-100 hover:bg-gray-200 font-bold text-gray-700 cursor-pointer">+</button>
              <span class="text-xs text-gray-400">(In Stock: ${p.quantity})</span>
            </div>
          </div>

          <!-- Toppings -->
          <div>
            <label class="block text-xs font-bold uppercase tracking-wider text-gray-700 mb-2">3. Toppings (+₹40 each)</label>
            <div class="grid grid-cols-2 gap-2">
              ${['Chilli', 'Onion', 'Tomato', 'Cheese'].map(top => {
                const checked = state.selectedToppings.includes(top);
                return `
                  <label class="flex items-center justify-between p-2 rounded-xl border text-xs cursor-pointer ${
                    checked ? 'border-amber-400 bg-amber-50/60 text-amber-900 font-bold' : 'border-gray-200 text-gray-700'
                  }">
                    <span class="flex items-center gap-2">
                      <input type="checkbox" ${checked ? 'checked' : ''} onchange="window.dominosToggleTopping('${top}')" />
                      ${top}
                    </span>
                    <span class="text-[11px] text-gray-400">+₹40</span>
                  </label>
                `;
              }).join('')}
            </div>
          </div>

          <!-- Drinks -->
          <div>
            <label class="block text-xs font-bold uppercase tracking-wider text-gray-700 mb-2">4. Drinks (+₹50 each)</label>
            <div class="grid grid-cols-2 gap-2">
              ${['Coca Cola', 'Sprite'].map(drink => {
                const checked = state.selectedDrinks.includes(drink);
                return `
                  <label class="flex items-center justify-between p-2 rounded-xl border text-xs cursor-pointer ${
                    checked ? 'border-emerald-400 bg-emerald-50/60 text-emerald-900 font-bold' : 'border-gray-200 text-gray-700'
                  }">
                    <span class="flex items-center gap-2">
                      <input type="checkbox" ${checked ? 'checked' : ''} onchange="window.dominosToggleDrink('${drink}')" />
                      ${drink}
                    </span>
                    <span class="text-[11px] text-gray-400">+₹50</span>
                  </label>
                `;
              }).join('')}
            </div>
          </div>

          <div class="pt-3 border-t flex items-center justify-between">
            <div>
              <span class="text-[10px] text-gray-400 uppercase font-bold block">Item Total</span>
              <span class="text-xl font-black text-[#006491]">₹${itemTotal.toFixed(2)}</span>
            </div>
            <button onclick="window.dominosAddItemToOrder()" class="py-2.5 px-6 bg-[#006491] hover:bg-[#00557c] text-white rounded-xl font-bold text-xs uppercase tracking-wider cursor-pointer">
              + Add to Order
            </button>
          </div>
        </div>
      </div>
    `;
  }

  function renderQrModal() {
    if (!state.showQrModal) return '';
    return `
      <div class="fixed inset-0 z-50 flex items-center justify-center bg-black/60 p-4 backdrop-blur-xs">
        <div class="bg-white rounded-2xl max-w-sm w-full p-6 text-center shadow-2xl border border-gray-200 space-y-4">
          <h3 class="text-lg font-black text-gray-900">Scan &amp; Pay via UPI</h3>
          <p class="text-xs text-gray-500">Google Pay, PhonePe, Paytm, BHIM</p>
          <div class="p-4 bg-gray-50 border rounded-2xl inline-block">
            <img src="/upi_qr.png" alt="UPI QR" class="w-48 h-48 object-contain mx-auto" />
          </div>
          <button onclick="window.dominosToggleQr(false)" class="w-full py-2.5 bg-[#006491] text-white rounded-xl text-xs font-bold cursor-pointer">
            Done &amp; Confirm
          </button>
        </div>
      </div>
    `;
  }

  function render() {
    const appEl = document.getElementById('app');
    if (!appEl) return;

    if (!state.currentUser) {
      appEl.innerHTML = renderLoginPage();
      return;
    }

    let mainContent = '';
    switch (state.currentView) {
      case 'ORDER': mainContent = renderPOS(); break;
      case 'STOCK': mainContent = renderStock(); break;
      case 'DAILY_SALE': mainContent = renderDailySales(); break;
      case 'MONTHLY_SALE': mainContent = renderMonthlySales(); break;
      case 'TOP_SELLING': mainContent = renderTopSelling(); break;
      case 'PRODUCT': mainContent = renderProducts(); break;
      case 'MANAGER': mainContent = renderStaff(); break;
      case 'ORDER_HISTORY': mainContent = renderOrderHistory(); break;
    }

    appEl.innerHTML = `
      <div class="min-h-screen bg-[#f4f7f6] text-gray-800 flex flex-col">
        ${renderNavbar()}
        <main class="flex-1 pb-16">
          ${mainContent}
        </main>
        ${renderReceiptModal()}
        ${renderCustomizeModal()}
        ${renderQrModal()}
        ${state.notification ? `
          <div class="fixed top-20 right-6 z-50 px-4 py-3 rounded-xl shadow-2xl text-white font-medium text-sm ${
            state.notification.type === 'success' ? 'bg-emerald-600' : 'bg-[#E31837]'
          }">
            ${state.notification.message}
          </div>
        ` : ''}
      </div>
    `;
  }

  // Window global handlers
  window.dominosSetView = function(view) { state.currentView = view; render(); };
  window.dominosLogout = function() { state.currentUser = null; render(); };
  window.dominosQuickLogin = function(username, role, fullName) {
    state.currentUser = { id: Date.now(), username, role, fullName };
    state.currentView = 'ORDER';
    render();
  };
  window.dominosHandleLogin = function(e) {
    e.preventDefault();
    const u = document.getElementById('loginUsername').value;
    const r = document.getElementById('loginRole').value;
    state.currentUser = { id: Date.now(), username: u, role: r, fullName: u };
    state.currentView = 'ORDER';
    render();
  };
  window.dominosSetCustName = function(val) { state.custName = val; };
  window.dominosSetSearch = function(val) { state.searchQuery = val; render(); };
  window.dominosSetCategory = function(cat) { state.selectedCategory = cat; render(); };
  window.dominosSetDiscount = function(val) { state.discountPercent = Math.max(0, Math.min(100, Number(val))); render(); };
  window.dominosSetPaymentMode = function(mode) { state.paymentMode = mode; render(); };
  window.dominosSetUpiId = function(val) { state.upiId = val; };
  window.dominosToggleQr = function(show) { state.showQrModal = show; render(); };
  window.dominosClearItems = function() { state.orderItems = []; render(); };
  window.dominosRemoveItem = function(idx) { state.orderItems.splice(idx, 1); render(); };
  window.dominosResetPos = function() { state.custName = ''; state.orderItems = []; state.discountPercent = 0; state.upiId = ''; render(); };

  window.dominosOpenCustomize = function(pizzaId) {
    const p = state.pizzas.find(x => x.pizzaId === pizzaId);
    if (!p || p.quantity <= 0) {
      showNotify('error', 'Pizza is out of stock!');
      return;
    }
    state.activePizza = p;
    state.activeSize = 'Medium';
    state.activeQty = 1;
    state.selectedToppings = [];
    state.selectedDrinks = [];
    render();
  };
  window.dominosCloseCustomize = function() { state.activePizza = null; render(); };
  window.dominosSetSize = function(size) { state.activeSize = size; render(); };
  window.dominosSetQty = function(delta) {
    if (!state.activePizza) return;
    state.activeQty = Math.max(1, Math.min(state.activePizza.quantity, state.activeQty + delta));
    render();
  };
  window.dominosToggleTopping = function(top) {
    const idx = state.selectedToppings.indexOf(top);
    if (idx === -1) state.selectedToppings.push(top);
    else state.selectedToppings.splice(idx, 1);
    render();
  };
  window.dominosToggleDrink = function(drink) {
    const idx = state.selectedDrinks.indexOf(drink);
    if (idx === -1) state.selectedDrinks.push(drink);
    else state.selectedDrinks.splice(idx, 1);
    render();
  };
  window.dominosAddItemToOrder = function() {
    if (!state.activePizza) return;
    const basePrice = getPriceForSize(state.activePizza, state.activeSize);
    const itemTotal = (basePrice * state.activeQty) + (state.selectedToppings.length * 40) + (state.selectedDrinks.length * 50);

    state.orderItems.push({
      pizzaId: state.activePizza.pizzaId,
      pizzaName: state.activePizza.name,
      category: state.activePizza.category,
      size: state.activeSize,
      quantity: state.activeQty,
      unitPrice: basePrice,
      toppings: [...state.selectedToppings],
      coldDrinks: [...state.selectedDrinks],
      itemTotal
    });

    state.activePizza = null;
    showNotify('success', 'Added to order!');
  };

  window.dominosFinalizeOrder = async function() {
    if (!state.custName.trim()) {
      showNotify('error', 'Please enter customer name');
      return;
    }
    if (state.orderItems.length === 0) {
      showNotify('error', 'Please select at least one pizza');
      return;
    }

    const orderReq = {
      customerName: state.custName.trim(),
      discountPercent: state.discountPercent,
      paymentMode: state.paymentMode === 'Cash' ? 0 : state.paymentMode === 'Online' ? 1 : 2,
      upiId: state.paymentMode === 'UPI' ? state.upiId : null,
      cashierUsername: state.currentUser.username,
      items: state.orderItems.map(i => ({
        pizzaId: i.pizzaId,
        size: i.size === 'Small' ? 0 : i.size === 'Medium' ? 1 : i.size === 'Large' ? 2 : 3,
        quantity: i.quantity,
        toppings: i.toppings,
        coldDrinks: i.coldDrinks
      }))
    };

    try {
      const createdOrder = await API.placeOrder(orderReq);
      try { confetti({ particleCount: 70, spread: 60, origin: { y: 0.6 } }); } catch (e) {}

      state.activeReceipt = createdOrder;
      state.custName = '';
      state.orderItems = [];
      state.discountPercent = 0;
      state.upiId = '';
      showNotify('success', `Order #${createdOrder.orderId} placed successfully!`);
      await loadData();
    } catch (err) {
      showNotify('error', err.message);
    }
  };

  window.dominosShowReceipt = function(idx) {
    if (state.orders.length > idx) {
      state.activeReceipt = state.orders[idx];
      render();
    }
  };
  window.dominosShowReceiptById = function(id) {
    const ord = state.orders.find(o => o.orderId === id);
    if (ord) {
      state.activeReceipt = ord;
      render();
    }
  };
  window.dominosCloseReceipt = function() { state.activeReceipt = null; render(); };

  window.dominosAddStock = async function(id, delta) {
    const p = state.pizzas.find(x => x.pizzaId === id);
    if (p) {
      await API.updateStock(id, p.quantity + delta);
      showNotify('success', `Added +${delta} to stock!`);
      await loadData();
    }
  };

  window.dominosCancelOrder = async function(id) {
    if (confirm(`Cancel order #${id} and restock items?`)) {
      const ok = await API.cancelOrder(id);
      if (ok) {
        showNotify('success', `Order #${id} cancelled and inventory restocked!`);
        await loadData();
      }
    }
  };

  window.dominosAddStaff = async function(e) {
    e.preventDefault();
    const u = document.getElementById('staffUsername').value.trim();
    const f = document.getElementById('staffFullName').value.trim();
    if (u) {
      const ok = await API.addManager(u, f);
      if (ok) {
        showNotify('success', `Staff member ${f || u} registered!`);
        document.getElementById('staffUsername').value = '';
        document.getElementById('staffFullName').value = '';
        await loadData();
      } else {
        showNotify('error', 'Username already exists');
      }
    }
  };

  window.dominosDeleteStaff = async function(id) {
    if (confirm('Delete this staff account?')) {
      await API.deleteManager(id);
      showNotify('success', 'Staff account removed');
      await loadData();
    }
  };

  window.dominosAddProduct = async function(e) {
    e.preventDefault();
    const name = document.getElementById('prodName').value.trim();
    const category = document.getElementById('prodCategory').value;
    const smallPrice = Number(document.getElementById('prodSmall').value);
    const mediumPrice = Number(document.getElementById('prodMedium').value);
    const largePrice = Number(document.getElementById('prodLarge').value);
    const extraLargePrice = Number(document.getElementById('prodXL').value);
    const quantity = Number(document.getElementById('prodQty').value);

    await API.addPizza({
      name, category, smallPrice, mediumPrice, largePrice, extraLargePrice, quantity,
      imagePath: '/default_pizza.png', description: `${category} specialty`
    });

    showNotify('success', `Added "${name}" to menu!`);
    await loadData();
  };

  window.dominosDeleteProduct = async function(id) {
    if (confirm('Remove this pizza from menu?')) {
      await API.deletePizza(id);
      showNotify('success', 'Pizza removed from menu');
      await loadData();
    }
  };

  // Start app
  loadData();
})();
