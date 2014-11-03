/*
  Copyright (C) 2010-2014 Modeling Virtual Environments and Simulation
  (MOVES) Institute at the Naval Postgraduate School (NPS)
  http://www.MovesInstitute.org and http://www.nps.edu
 
  This file is part of Mmowgli.
  
  Mmowgli is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  any later version.

  Mmowgli is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with Mmowgli in the form of a file named COPYING.  If not,
  see <http://www.gnu.org/licenses/>
*/

package edu.nps.moves.mmowgli.utility;

import java.util.concurrent.*;

public class MThreadManager
{
  private MThreadManager(){} // not instanciable
  
  private static ExecutorService pool = Executors.newCachedThreadPool();

  public static void run(Runnable runner)
  {
    pool.execute(new Preamble(runner));
  }
  
  public static void run(Runnable runner, boolean wait)
  {
    Future<?> f = pool.submit(runner);
    if(wait)
      try {
        f.get();
      }
      catch(InterruptedException | ExecutionException ex) {
        System.err.println("Exception waiting for thread completion in MThreadManager: "+ex.getLocalizedMessage());
      }

  }
  /* This is a little shim which makes sure our priorities are straight */
  /* Should be minimally expensive to make and destroy */
  private static class Preamble implements Runnable
  {
    private Runnable runner;

    public Preamble(Runnable runner)
    {
      this.runner = runner;
    }
    @Override
    public void run()
    {
      Thread thr = Thread.currentThread();
      thr.setPriority(Thread.NORM_PRIORITY); 
      //thr.setDaemon(true);
      thr.setName("MThreadManagerPoolThread");
      runner.run();
    }
  }
  
  static private int UP_PRIORITY = 0;
  
  static {
    int mx = Thread.MAX_PRIORITY;
    int mn = Thread.MIN_PRIORITY;
    UP_PRIORITY = (mx>mn?+1:-1);    
  }
  public static void priorityNormalPlus1(Thread t)
  {
    t.setPriority(Thread.NORM_PRIORITY);
    priorityUp(t);
  }
  public static void priorityNormalLess1(Thread t)
  {
    t.setPriority(Thread.NORM_PRIORITY);
    priorityDown(t);
  }
  public static void priorityUp()
  {
    priorityUp(Thread.currentThread());
  }
  public static void priorityUp(Thread t)
  {
    t.setPriority(t.getPriority()+UP_PRIORITY);
  }
  public static void priorityDown()
  {
    priorityDown(Thread.currentThread());
  }
  public static void priorityDown(Thread t)
  {
    t.setPriority(t.getPriority()-UP_PRIORITY);    
  }
}
