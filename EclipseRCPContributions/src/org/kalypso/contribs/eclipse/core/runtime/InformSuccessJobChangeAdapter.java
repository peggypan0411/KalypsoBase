package org.kalypso.contribs.eclipse.core.runtime;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * Waits for {@link org.eclipse.core.runtime.jobs.Job}to finish and then shows a message box if the job was succesful.
 * 
 * @see IStatus#isOK()
 * @author belger
 */
public final class InformSuccessJobChangeAdapter extends JobChangeAdapter
{
  protected final Shell m_shell;

  protected final String m_messageTitle;

  protected final String m_messageFirstline;

  /**
   * @param shell
   *          Shell to show MessageBox with. Must not be null.
   * @param messageTitle
   *          Displayed in the title of the message box.
   * @param messageFirstline
   *          Displayed as first line of the message box. The second line wil be the mesage of the status objekt.
   */
  public InformSuccessJobChangeAdapter( final Shell shell, final String messageTitle, final String messageFirstline )
  {
    m_shell = shell;
    m_messageTitle = messageTitle;
    m_messageFirstline = messageFirstline;
  }

  /**
   * @see org.eclipse.core.runtime.jobs.JobChangeAdapter#done(org.eclipse.core.runtime.jobs.IJobChangeEvent)
   */
  @Override
  public void done( final IJobChangeEvent event )
  {
    final IStatus status = event.getResult();
    if( status.isOK() )
    {
      final Runnable runnable = new Runnable()
      {
        @Override
        public void run( )
        {
          MessageDialog.openInformation( m_shell, m_messageTitle, m_messageFirstline + "\n" + status.getMessage() );

        }
      };
      m_shell.getDisplay().asyncExec( runnable );
    }
  }
}