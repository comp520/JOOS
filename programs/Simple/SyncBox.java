public class SyncBox {
  protected Object boxContents;

  public SyncBox() { super(); }

  public synchronized Object get()
    { Object contents;
      contents = boxContents;
      boxContents = null;
      return contents;
     }

  public synchronized boolean put (Object contents)
    { if (boxContents != null)
        return false;
      boxContents = contents;
      return true;
    }
}
