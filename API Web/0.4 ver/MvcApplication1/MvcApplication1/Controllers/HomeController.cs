using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using System.Configuration;
using System.Data.SqlClient;

namespace MvcApplication1.Controllers
{
    public class HomeController : Controller
    {
        //
        // GET: /Home/

        public ActionResult Index()
        {
            return View();
        }

        //
        // GET: /Home/DBSet?
        public JsonResult DBSet(String phone_num, String file_name ,String file_id, String category, String download_date)
        {
            if (!(phone_num != null && file_name != null && file_id != null && download_date != null))
            {
                return Json(new { result = 2}, JsonRequestBehavior.AllowGet);
            }

            phone_num = phone_num.Replace(" ", "");

            if (phone_num == "")
            {
                return Json(new { result = 2 }, JsonRequestBehavior.AllowGet);
            }

            String insertCmd = "insert into TB_YOUTUBE_DOWNLOAD(phonenumber,filename,fileid,category,downloaddate) values('" + phone_num + "','" + file_name +"','" + file_id + "','" + category + "','" + download_date + "')";

            String dbConf = ConfigurationManager.ConnectionStrings["YoutubeDBContext"].ConnectionString;
            SqlConnection conn = new SqlConnection(dbConf);

            try
            {
                conn.Open();
                SqlCommand cmd;
                cmd = new SqlCommand(insertCmd, conn);
                cmd.ExecuteNonQuery();
                conn.Close();
            }
            catch(Exception e)
            {
                return Json(new { result = 2}, JsonRequestBehavior.AllowGet);
            }
            finally
            {
                conn.Close();
            }

            return Json(new { result = 1 }, JsonRequestBehavior.AllowGet);
        }
    }
}
