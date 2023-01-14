package com.fusionscoutingapp;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.image.BufferedImage;
// from https://stackoverflow.com/questions/4552045/copy-bufferedimage-to-clipboard
public class CopyImagetoClipBoard implements ClipboardOwner {

    public void copyImage(BufferedImage bi)
    {
        TransferableImage trans = new TransferableImage(bi);
        Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
        c.setContents( trans, this );
    }

    public void lostOwnership( Clipboard clip, Transferable trans ) {
        System.out.println( "Lost Clipboard Ownership" );
    }

    private static class TransferableImage implements Transferable {

        Image i;

        public TransferableImage( Image i ) {
            this.i = i;
        }

        public Object getTransferData( DataFlavor flavor )
                throws UnsupportedFlavorException {
            if ( flavor.equals( DataFlavor.imageFlavor ) && i != null ) {
                return i;
            }
            else {
                throw new UnsupportedFlavorException( flavor );
            }
        }

        public DataFlavor[] getTransferDataFlavors() {
            DataFlavor[] flavors = new DataFlavor[ 1 ];
            flavors[ 0 ] = DataFlavor.imageFlavor;
            return flavors;
        }

        public boolean isDataFlavorSupported( DataFlavor flavor ) {
            DataFlavor[] flavors = getTransferDataFlavors();
            for (DataFlavor dataFlavor : flavors) {
                if (flavor.equals(dataFlavor)) {
                    return true;
                }
            }

            return false;
        }
    }
}