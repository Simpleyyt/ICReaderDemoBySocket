package com.DNet;

import org.xvolks.jnative.JNative;
import org.xvolks.jnative.Type;
import org.xvolks.jnative.pointers.Pointer;
import org.xvolks.jnative.pointers.memory.MemoryBlockFactory;


public class DNetAPI {
	private static String DNetLib = "DNet.dll";
	
	public static String errorMsg(int code)
	{
		 switch(code)
		 {
		 case 0:
			 return "普通错误";
		 case 100:
			 return "获取设备信息失败";
		 case 102:
			 return "密码错误";
		 case 103:
			 return "连接失败";
		 case 104:
			 return "命令没有响应";
		 case 105:
			 return "没有登录";
		 case 106:
			 return "无效的命令码";
		 case 107:
			 return "参数格式不对";
		 case 108:
			 return "参数长度不对";
		 default:
			 return "错误码无效";
		 }
	}
	
	private static Pointer getPointer(byte[] s) {
		try {
			Pointer pointer = new Pointer(
					MemoryBlockFactory.createMemoryBlock(s.length));
			for (int i = 0; i < s.length; i++) {
				pointer.setByteAt(i, s[i]);
			}
			return pointer;
		} catch (Exception e) {
			System.out.print("Failed to get Pointer Data!");
			return null;
		}
	}
	
	private static Pointer getPointer(int[] s) {
		try {
			Pointer pointer = new Pointer(
					MemoryBlockFactory.createMemoryBlock(s.length*4));
			for (int i = 0; i < s.length; i++) {
				pointer.setIntAt(i, s[i]);
			}
			return pointer;
		} catch (Exception e) {
			System.out.print("Failed to get Pointer Data!");
			return null;
		}
	}

	private static void getPointerData(Pointer p, byte[] s) {
		try {
			for (int i = 0; i < p.getSize(); i++) {
				s[i] = p.getAsByte(i);
			}
		} catch (Exception e) {
			System.out.print("Failed to get Pointer Data!");
		}
	}
	
	private static void getPointerData(Pointer p, int[] s) {
		try {
			for (int i = 0; i < p.getSize()/4; i++) {
				s[i] = p.getAsInt(i);
			}
		} catch (Exception e) {
			System.out.print("Failed to get Pointer Data!");
		}
	}

	private static int loadCommand(String functionName, int... parameter) {
		JNative n = null;
		try {
			n = new JNative(DNetLib, functionName);
			n.setRetVal(Type.INT);
			int i = 0;
			for (int p : parameter) {
				n.setParameter(i++, p);
			}
			n.invoke();
			return Integer.parseInt(n.getRetVal());
		} catch (Exception e) {
			System.out.print("Failed to load libfunction.so");
			return 1;
		}
	}

	public static int DN_SearchAll() {
		int result = loadCommand("DN_SearchAll");
		return result;
	}
	
	public static int DN_Search(byte[] szip) {
		Pointer szipPtr = getPointer(szip);
		int result = loadCommand("DN_Search", szipPtr.getPointer());
		return result;
	}
	
	public static int DN_GetSearchDev(byte[] szip,byte[] szver,byte[] szmac, byte[] pdevtype, byte[] pipmode, int[] ptcpport) {
		Pointer szipPtr = getPointer(szip);
		Pointer szverPtr = getPointer(szver);
		Pointer szmacPtr = getPointer(szmac);
		Pointer pdevtypePtr = getPointer(pdevtype);
		Pointer pipmodePtr = getPointer(pipmode);
		Pointer ptcpportPtr = getPointer(ptcpport);
		
		int result = loadCommand("DN_GetSearchDev",szipPtr.getPointer(),szverPtr.getPointer(),szmacPtr.getPointer(),pdevtypePtr.getPointer(),pipmodePtr.getPointer(),ptcpportPtr.getPointer());
		
		getPointerData(szipPtr, szip);
		getPointerData(szverPtr, szver);
		getPointerData(szmacPtr, szmac);
		getPointerData(pdevtypePtr, pdevtype);
		getPointerData(pipmodePtr, pipmode);
		getPointerData(ptcpportPtr, ptcpport);
		return result;
	}

	public static int DN_GetDevInfoUDPbyMACAndIP(byte[] szmac, byte[] szip, byte devtype) {
		Pointer szmacPtr = getPointer(szmac);
		Pointer szipPtr = getPointer(szip);
		
		int result = loadCommand("DN_GetDevInfoUDPbyMACAndIP",szmacPtr.getPointer(),szipPtr.getPointer(),devtype);
		
		getPointerData(szmacPtr,szmac);
		getPointerData(szipPtr,szip);
		return result;
	}

	public static int DN_GetDevConfigUDP(byte[] szname, byte[]szval) {
		Pointer sznamePtr = getPointer(szname);
		Pointer szvalPtr = getPointer(szval);
		
		int result = loadCommand("DN_GetDevConfigUDP", sznamePtr.getPointer(),szvalPtr.getPointer());

		getPointerData(sznamePtr,szname);
		getPointerData(szvalPtr,szval);
		return result;
	}
	
	public static int DN_ResetModifyConfigUDP() {
		int result = loadCommand("DN_ResetModifyConfigUDP");
		return result;
	}
	
	public static int DN_SetModifyConfigUDP(byte[] szname,byte[] szval) {
		Pointer sznamePtr = getPointer(szname);
		Pointer szvalPtr = getPointer(szval);
		int result = loadCommand("DN_SetModifyConfigUDP", sznamePtr.getPointer(),szvalPtr.getPointer());
		return result;
	}

	public static int DN_ModifyDevUDPbyMACAndIP(byte[] szmac,byte[] szip, byte[] szpwd, byte devtype) {
		Pointer szmacPtr = getPointer(szmac);
		Pointer szipPtr = getPointer(szip);
		Pointer szpwdPtr = getPointer(szpwd);
		
		int result = loadCommand("DN_ModifyDevUDPbyMACAndIP", szmacPtr.getPointer(),szipPtr.getPointer(),szpwdPtr.getPointer(),devtype);
		
		return result;
	}


	public static int DN_SendCmd(int devtype,byte[] szip,int port,byte[] szname,byte[] szval,byte budp) {
		Pointer szipPtr = getPointer(szip);
		Pointer sznamePtr = getPointer(szname);
		Pointer szvalPtr = getPointer(szval);
		
		int result = loadCommand("DN_SendCmd", devtype, szipPtr.getPointer(),port,sznamePtr.getPointer(), szvalPtr.getPointer(),budp);
		
		return result;
	}

}