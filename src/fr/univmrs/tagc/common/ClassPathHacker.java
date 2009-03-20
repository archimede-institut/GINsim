package fr.univmrs.tagc.common;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;


/**
 * Update classpath at runtime.
 * code from http://forums.sun.com/thread.jspa?threadID=300557
 * added a method to add jar files in a set of directories at once
 */
public class ClassPathHacker {
    public URLClassLoader cl = new URLClassLoader(null);
    private static final Class[] parameters = new Class[]{URL.class};
     
    public static void addFile(String s) throws IOException {
        File f = new File(s);
        addFile(f);
    }
    public static void addFile(File f) throws IOException {
        addURL(f.toURI().toURL());
    }
    public static void addURL(URL u) throws IOException {
        URLClassLoader sysloader = (URLClassLoader)ClassLoader.getSystemClassLoader();
        Class sysclass = URLClassLoader.class;
     
        try {
            Method method = sysclass.getDeclaredMethod("addURL",parameters);
            method.setAccessible(true);
            method.invoke(sysloader,new Object[]{ u });
            System.out.println("added lib file: " + u);
        } catch (Throwable t) {
            t.printStackTrace();
            throw new IOException("Error, could not add URL to system classloader");
        }       
    }
    private static List l = new ArrayList();

    private static void listAdd(String s) {
        l.add(s);
    }
    
    public ClassLoader getClassLoader() {
        URL[] t = new URL[l.size()];
        for (int i=0 ; i<t.length ; i++) {
            File f = new File((String)l.get(i));
            if (!(f.exists() && f.canRead())) {
                continue;
            }
            try {
                t[i] = f.toURI().toURL();
            } catch (MalformedURLException e) {}
        }
        return new URLClassLoader(t);
    }
    
    public static void updateClassPath(String[] files) {
        for (int i=0 ; i<files.length ; i++) {
            File f = new File(files[i]);
            if (!(f.exists() && f.canRead())) {
                continue;
            }
            if (f.isDirectory()) {
                String[] content = f.list();
                for (int j=0 ; j<content.length ; j++) {
                    if (content[j].endsWith(".jar")) {
                        listAdd(f + File.pathSeparator + content[j]);
                    }
                }
            } else {
                listAdd(files[i]);
            }
        }
    }
}
