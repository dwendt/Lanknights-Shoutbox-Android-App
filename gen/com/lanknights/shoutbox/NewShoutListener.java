/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: O:\\working\\lanknightsshoutbox\\aidl\\com\\lanknights\\shoutbox\\NewShoutListener.aidl
 */
package com.lanknights.shoutbox;
public interface NewShoutListener extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.lanknights.shoutbox.NewShoutListener
{
private static final java.lang.String DESCRIPTOR = "com.lanknights.shoutbox.NewShoutListener";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.lanknights.shoutbox.NewShoutListener interface,
 * generating a proxy if needed.
 */
public static com.lanknights.shoutbox.NewShoutListener asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.lanknights.shoutbox.NewShoutListener))) {
return ((com.lanknights.shoutbox.NewShoutListener)iin);
}
return new com.lanknights.shoutbox.NewShoutListener.Stub.Proxy(obj);
}
public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_handleShoutsUpdate:
{
data.enforceInterface(DESCRIPTOR);
this.handleShoutsUpdate();
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.lanknights.shoutbox.NewShoutListener
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
public void handleShoutsUpdate() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_handleShoutsUpdate, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_handleShoutsUpdate = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
}
public void handleShoutsUpdate() throws android.os.RemoteException;
}
