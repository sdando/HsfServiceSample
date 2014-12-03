package com.taobao.hsf.test;



import com.taobao.carts.constant.CartFrom;
import com.taobao.carts.dataobject.CartItemDO;
import com.taobao.carts.dataobject.CartQuery;
import com.taobao.carts.dataobject.OperateCartItem;
import com.taobao.carts.service.CartWapService;
import com.taobao.hsf.hsfunit.HSFEasyStarter;
import com.taobao.hsf.hsfunit.util.ServiceUtil;
import com.taobao.hsf.test.service.PseudoHTaoService;
import com.taobao.tradebase.view.OperateResult;
import com.taobao.tradebase.view.cart.AddCartResult;
import com.taobao.tradebase.view.cart.CartBundleView;
import com.taobao.tradebase.view.cart.CartMainView;
import com.taobao.tradebase.view.cart.CartOutLine;
import com.taobao.tradebase.view.cart.CartSubView;
import com.taobao.uic.common.domain.ResultDO;
import com.taobao.uic.common.service.userinfo.UicReadService;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * HSFUnit测试。
 * 
 * @author bogao <bogao.zx@alibaba-inc.com>
 * @version 1.0.0 <2014年11月18日 下午1:34:23>
 * @since JDK1.6
 */
public class PseudoHTaoTest {
	
	ApplicationContext context;
	
	@Before
	public void before() {
		HSFEasyStarter.startWithVersion("2.1.0.7");
		context = new ClassPathXmlApplicationContext("PseudoHTaoService.xml");
	}

	@Test
	public void testQueryInfo() {
        PseudoHTaoService pseudoHTaoService = (PseudoHTaoService)context.getBean("pseudoHTaoService");
        ServiceUtil.waitServiceReady(pseudoHTaoService);
        String result = pseudoHTaoService.queryInfo("zx");
        assertEquals("123", result);
	}
	
	/**
	 * 根据用户昵称获取userId
	 * tbtest001:2026680875
	 * zhenyu001:176252174
	 */
	@Test
	public void testGetUserIdByMemberId() {
		UicReadService uicReadService = (UicReadService)context.getBean("uicReadService");
		ServiceUtil.waitServiceReady(uicReadService);
		String user = "zhenyu001";
		//getUserIdByNick的第二个参数为appName，貌似可选
		ResultDO<Long> resultDO = uicReadService.getUserIdByNick(user, "");
		if(resultDO.isSuccess()) {
			System.out.println("UserName: " + user + "\nUserID: " + resultDO.getModule());
		}	
	}
	
	/**
	 * 根据userId,查询用户购物车信息
	 */
	@Test
	public void testQueryCartInfo() {
        CartWapService cartService = (CartWapService)context.getBean("cartService");
        ServiceUtil.waitServiceReady(cartService);
        long buyerId = 2026680875;
        CartQuery cartQuery = new CartQuery();
        //CartQuery中的UserId、CartFrom为必填项(CartFrom标识应用的来源，现在没有海淘)
        cartQuery.setUserId(buyerId);
        cartQuery.setCartFrom(CartFrom.TAOBAO_CLIENT_V2);
        CartOutLine cartResult = cartService.queryCartList(buyerId, cartQuery);
        if(cartResult == null) {
        	System.out.println("return is null");
        	return;
        }
        
        for(CartBundleView cartBundleView: cartResult.list) {
        	List<CartMainView> cartMainViews = cartBundleView.main;
        	for(CartMainView cartMainView: cartMainViews){
        		List<CartSubView> cartSubViews = cartMainView.orders;
        		for(CartSubView cartSubView: cartSubViews) {
        			System.out.println("CartId:" + cartSubView.cartId);
        			System.out.println("ItemId:" + cartSubView.itemId);
        			System.out.println("SkuId: " + cartSubView.skuId);
        			System.out.println("Price: " + cartSubView.price.now/100 );
        			System.out.println("Quantity: " + cartSubView.amount.now);
        			System.out.println();
        		} 
        		System.out.println("-------------------");
        	}
        	System.out.println("******************");
        }         
	}
	
	/**
	 * 向购物车中添加商品
	 * CartItemDO中下面属性都为必填项
	 * userId  当前用户数字id
     * itemId  当前商品的数字id
     * skuId   当前商品的sku
     * quantity 商品购买数量
	 */
	@Test
	public void testAddCart() {
        CartWapService cartService = (CartWapService)context.getBean("cartService");
        ServiceUtil.waitServiceReady(cartService);
        
        long buyerId = 2026680875; //tbtest001的ID
        long itemId = 2100528250198L; 
        long skuId = 0;
        int quantity = 2;
        
        CartItemDO cartItemDO = new CartItemDO();
        cartItemDO.setUserId(buyerId);
        cartItemDO.setItemId(itemId);
        cartItemDO.setSkuId(skuId);
        cartItemDO.setQuantity(quantity);
        
        AddCartResult addCartResult = cartService.addCart(buyerId, cartItemDO);
        
        if(addCartResult.isSuccess()) {
        	System.out.println("cartId: " + addCartResult.getCartId());
        	System.out.println("add item successfully!");
        }
        else {
			System.out.println("error code: " + addCartResult.getErrorCode());
			System.out.println("error message: " + addCartResult.getError());
		}
	}
	
	/**
	 * 更新购物车中商品的数量
	 */
	@Test
	public void testUpdateCart() {
        CartWapService cartService = (CartWapService)context.getBean("cartService");
        ServiceUtil.waitServiceReady(cartService);
        long buyerId = 2026680875; 
        int quantity = 3;
        long cartId = 1000045542212L;
        OperateCartItem operateCartItem = new OperateCartItem();
        operateCartItem.setUserId(buyerId);
        
        List<CartItemDO> operateList = new ArrayList<CartItemDO>();
        CartItemDO cartItemDO = new CartItemDO();
        //userId,cartId必填
        cartItemDO.setUserId(buyerId);
        cartItemDO.setCartId(cartId);
        cartItemDO.setQuantity(quantity);
        operateList.add(cartItemDO);
        List<CartItemDO> operateRealList = new ArrayList<CartItemDO>();
        
        operateCartItem.setOperateList(operateList);
        operateCartItem.setOperateRelaList(operateRealList);
        
        OperateResult operateResult = cartService.updateCart(buyerId, operateCartItem);
        if(operateResult.success == true) {
        	System.out.println("update successfully！");
        }
        else {
        	System.out.println("error:" + operateResult.error.getError());
        }
	}

	/**
	 * 删除购物车中的商品
	 */
	@Test
	public void testDeleteCart() {
        CartWapService cartService = (CartWapService)context.getBean("cartService");
        ServiceUtil.waitServiceReady(cartService);
        long buyerId = 2026680875; 
//      long itemId = 1500010788592L; 
        long cartId = 1000045904232L;
        OperateCartItem operateCartItem = new OperateCartItem();
        operateCartItem.setUserId(buyerId);
        
        List<CartItemDO> operateList = new ArrayList<CartItemDO>();
        CartItemDO cartItemDO = new CartItemDO();
        cartItemDO.setCartId(cartId) ;
        cartItemDO.setUserId(buyerId);
//      cartItemDO.setItemId(itemId);
        operateList.add(cartItemDO);
        //RealList也必须设置，坑
        List<CartItemDO> operateRealList = new ArrayList<CartItemDO>(operateList);
        
        operateCartItem.setOperateList(operateList);
        operateCartItem.setOperateRelaList(operateRealList);
        
        OperateResult operateResult = cartService.deleteCart(buyerId, operateCartItem);
        if(operateResult.success == true) {
        	System.out.println("delete successfully！");
        }
        else {
        	System.out.println("error" + operateResult.error.getError());
        }
	}
}
