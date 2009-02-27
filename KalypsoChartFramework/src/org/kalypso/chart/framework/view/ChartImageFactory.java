package org.kalypso.chart.framework.view;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.kalypso.chart.framework.model.mapper.IAxis;
import org.kalypso.chart.framework.model.mapper.component.IAxisComponent;
import org.kalypso.chart.framework.model.mapper.registry.IMapperRegistry;
import org.kalypso.chart.framework.model.mapper.renderer.IAxisRenderer;
import org.kalypso.contribs.eclipse.swt.graphics.GCWrapper;

/**
 * @author burtscher Creates an Image from a chart widget. The objects has to be disposed when it's no longer needed
 */
public class ChartImageFactory
{

  public enum IMAGE_TYPE
  {
    PNG,
    JPG,
    GIF
  }

  /**
   * @param transparency
   *            RGB to which the transparency-color should be set; transparency will not be used if RGB is null
   */
  @SuppressWarnings("unchecked")
  public static ImageData createChartImage( ChartComposite chart, Device dev, int width, int height )
  {
    chart.setSize( new Point( width, height ) );
    chart.redraw();

    final Image img = new Image( dev, width, height );

    final GCWrapper gcw = new GCWrapper( new GC( img ) );
    Image tmpImg = new Image( dev, width, height );
    GC tmpGc = new GC( tmpImg );
    GCWrapper tmpGcw = new GCWrapper( tmpGc );

    tmpGcw.setBackground( dev.getSystemColor( SWT.COLOR_YELLOW ) );
    tmpGcw.fillRectangle( 0, 0, tmpImg.getBounds().width, tmpImg.getBounds().height );

    // img mit weißem Hintergrund versehen
    gcw.setBackground( dev.getSystemColor( SWT.COLOR_WHITE ) );
    gcw.fillRectangle( 0, 0, width, height );

    // Plot zeichnen
    final PlotCanvas plotCanvas = chart.getPlot();
    tmpImg = plotCanvas.paintBuffered( tmpGcw, img.getBounds(), null );
    gcw.drawImage( tmpImg, 0, 0, plotCanvas.getBounds().width, plotCanvas.getBounds().height, plotCanvas.getBounds().x, plotCanvas.getBounds().y, plotCanvas.getBounds().width, plotCanvas.getBounds().height );
    tmpGcw.fillRectangle( 0, 0, tmpImg.getBounds().width, tmpImg.getBounds().height );

    final IMapperRegistry ar = chart.getModel().getMapperRegistry();
    final Map<IAxis< ? >, IAxisComponent> components = ar.getAxesToComponentsMap();
    final Set<Entry<IAxis< ? >, IAxisComponent>> acs = components.entrySet();
    for( final Entry<IAxis< ? >, IAxisComponent> ac : acs )
    {
      final IAxis axis = ac.getKey();
      // Casten, damit man auf die Component-Eigenschaften zugreifen kann
      final AxisCanvas comp = (AxisCanvas) ac.getValue();
      final IAxisRenderer rend = ar.getRenderer( axis ); // axis.getRgetRenderer();

      // Wenn man den GC nicht neu erzeugt, werden die AChsen nicht gezeichnet, sondern nochmal das Chart
      // Zusätzlich muss das Image disposed werden, weil sonst noch Teile des Plot in die AxisSpaces gezeichnet werden
      tmpImg.dispose();
      tmpImg = new Image( dev, width, height );
      tmpGc.dispose();
      tmpGcw.dispose();
      tmpGc = new GC( tmpImg );
      tmpGcw = new GCWrapper( tmpGc );
      // den Renderer in den TmpGC-Zeichnen lassen
      rend.paint( tmpGcw, axis, comp.getBounds() );
      /*
       * ...und ins Endbild kopieren; dabei muss berücksichtigt werden, dass die Components in 2 Ebenen Tiefe liegen -
       * zur Position muss noch die der Eltern addiert werden
       */
      gcw.drawImage( tmpImg, comp.getBounds().x, comp.getBounds().y, comp.getBounds().width, comp.getBounds().height, comp.getParent().getBounds().x + comp.getBounds().x, comp.getParent().getBounds().y
          + comp.getBounds().y, comp.getBounds().width, comp.getBounds().height );
    }
    gcw.dispose();
    tmpGc.dispose();
    tmpGcw.dispose();
    tmpImg.dispose();

    final ImageData id = img.getImageData();

    img.dispose();

    return id;

  }
}
